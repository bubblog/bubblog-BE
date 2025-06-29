package Bubble.bubblog.domain.user.controller;

import Bubble.bubblog.domain.user.dto.authRes.LoginResponseDTO;
import Bubble.bubblog.domain.user.dto.authRes.ReissueResponseDTO;
import Bubble.bubblog.domain.user.dto.authRes.TokensDTO;
import Bubble.bubblog.domain.user.dto.req.LoginRequestDTO;
import Bubble.bubblog.domain.user.dto.req.SignupRequestDTO;
import Bubble.bubblog.domain.user.entity.User;
import Bubble.bubblog.domain.user.service.UserService;
import Bubble.bubblog.global.dto.ErrorResponse;
import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.UUID;

@Tag(name = "User Auth", description = "유저 인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/auth", produces = "application/json")
public class AuthController {

    private final UserService userService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않거나 중복된 이메일입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/signup")
    public SuccessResponse<Void> signup(@RequestBody SignupRequestDTO request) {
        userService.signup(request);
        return SuccessResponse.of();
    }

    @Operation(summary = "로그인", description = "로그인을 시도합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "이메일 또는 비밀번호가 잘못되었습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public SuccessResponse<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request, HttpServletResponse response) {
        User user = userService.login(request); // User 객체 반환
        TokensDTO tokens = userService.issueTokens(user.getId());

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .secure(true) // HTTPS 환경에서는 true
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("none")
                .build();

        // 쿠키 수동으로 삽입
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return SuccessResponse.of(new LoginResponseDTO(tokens.getAccessToken(), user.getId()));
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "로그아웃", description = "리프레시 토큰을 쿠키와 Redis에서 삭제하고 로그아웃합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 로그아웃 요청입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/logout")
    public SuccessResponse<Void> logout(@Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
                                    @CookieValue("refreshToken") String refreshToken,
                                    HttpServletResponse response) {

        userService.logout(userId); // Redis에서 삭제

        // 쿠키 제거 (maxAge=0)
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("none")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return SuccessResponse.of();
    }

    @Operation(summary = "토큰 재발급", description = "리프레시 토큰을 검증하고 새 액세스 토큰 및 리프레시 토큰을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰이 유효하지 않거나 만료됨",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })

    @PostMapping("/reissue")
    public SuccessResponse<ReissueResponseDTO> reissue(@CookieValue("refreshToken") String refreshToken,
                                                       HttpServletResponse response) {       // @RequestBody ReissueRequestDTO request
        TokensDTO newtokens = userService.reissueTokens(refreshToken);
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", newtokens.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("none")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return SuccessResponse.of(new ReissueResponseDTO(newtokens.getAccessToken(), newtokens.getUserId()));
    }

}
