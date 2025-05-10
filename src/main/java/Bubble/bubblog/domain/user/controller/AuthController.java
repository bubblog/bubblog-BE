package Bubble.bubblog.domain.user.controller;

import Bubble.bubblog.domain.user.dto.AccessTokenDTO;
import Bubble.bubblog.domain.user.dto.LoginRequestDTO;
import Bubble.bubblog.domain.user.dto.SignupRequestDTO;
import Bubble.bubblog.domain.user.dto.TokensDTO;
import Bubble.bubblog.domain.user.service.UserService;
import Bubble.bubblog.global.service.TokenService;
import Bubble.bubblog.global.util.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.UUID;

@Tag(name = "User Auth", description = "유저 인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDTO request) {
        userService.signup(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<AccessTokenDTO> login(@RequestBody LoginRequestDTO request) {
        TokensDTO tokens = userService.login(request);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .secure(false) // HTTPS 환경에서는 true
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(new AccessTokenDTO(tokens.getAccessToken()));
    }

    @Operation(summary = "로그아웃")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal UUID userId,
                                    @CookieValue("refreshToken") String refreshToken) {
        userService.logout(userId); // Redis에서 삭제

        // 쿠키 제거 (maxAge=0)
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<AccessTokenDTO> reissue(@CookieValue("refreshToken") String refreshToken) {       // @RequestBody ReissueRequestDTO request
        TokensDTO newtokens = userService.reissueTokens(refreshToken);
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", newtokens.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(new AccessTokenDTO(newtokens.getAccessToken()));
    }


}
