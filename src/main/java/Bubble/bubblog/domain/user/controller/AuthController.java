package Bubble.bubblog.domain.user.controller;

import Bubble.bubblog.domain.user.dto.LoginRequestDTO;
import Bubble.bubblog.domain.user.dto.ReissueRequestDTO;
import Bubble.bubblog.domain.user.dto.SignupRequestDTO;
import Bubble.bubblog.domain.user.dto.TokensDTO;
import Bubble.bubblog.domain.user.service.UserService;
import Bubble.bubblog.global.service.TokenService;
import Bubble.bubblog.global.util.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<TokensDTO> login(@RequestBody LoginRequestDTO request) {
        TokensDTO tokens = userService.login(request);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal String userId) {
        userService.logout(userId);
        return ResponseEntity.ok("로그아웃 완료");
    }
    
    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<TokensDTO> reissue(@RequestBody ReissueRequestDTO request) {
        TokensDTO newtokens = userService.reissueTokens(request.getRefreshToken());
        return ResponseEntity.ok(newtokens);
    }


}
