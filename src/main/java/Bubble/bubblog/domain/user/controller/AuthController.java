package Bubble.bubblog.domain.user.controller;

import Bubble.bubblog.domain.user.dto.LoginRequestDTO;
import Bubble.bubblog.domain.user.dto.SignupRequestDTO;
import Bubble.bubblog.domain.user.entity.User;
import Bubble.bubblog.domain.user.service.UserService;
import Bubble.bubblog.global.service.TokenService;
import Bubble.bubblog.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDTO request) {
        userService.signup(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        User user = userService.login(request);

        String accessToken = jwtUtil.generateAccessToken(user.getId().toString());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId().toString());

        tokenService.saveRefreshToken(user.getId().toString(), refreshToken);

        // Map.of()는 json형식으로 반환
        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        String userId = jwtUtil.extractUserId(token.substring(7)); // Bearer 제거
        tokenService.deleteRefreshToken(userId);
        return ResponseEntity.ok("로그아웃 완료");
    }
    
    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody Map<String, String> req) {
        String refreshToken = req.get("refreshToken");

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 리프레시 토큰");
        }

        String userId = jwtUtil.extractUserId(refreshToken);
        String storedToken = tokenService.getRefreshToken(userId);

        if (!refreshToken.equals(storedToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 불일치");
        }

        String newAccessToken = jwtUtil.generateAccessToken(userId);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId);
        tokenService.saveRefreshToken(userId, newRefreshToken);

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken
        ));
    }


}
