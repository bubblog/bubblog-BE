package Bubble.bubblog.domain.user.service;

import Bubble.bubblog.domain.user.dto.LoginRequestDTO;
import Bubble.bubblog.domain.user.dto.SignupRequestDTO;
import Bubble.bubblog.domain.user.dto.TokensDTO;
import Bubble.bubblog.domain.user.entity.User;
import Bubble.bubblog.domain.user.repository.UserRepository;
import Bubble.bubblog.global.service.TokenService;
import Bubble.bubblog.global.util.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    // signup
    public void signup(SignupRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이메일 중복");
        }

        User user = User.from(request, passwordEncoder);

        userRepository.save(user);
    }

    // login
    public TokensDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("이메일 없음"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("비밀번호 틀림");
        }

        return issueTokens(user.getId().toString());
    }

    // logout
    public void logout(String token) {
        String userId = jwtUtil.extractUserId(token.substring(7)); // Bearer 제거
        tokenService.deleteRefreshToken(userId);
    }

    // reissue tokens cuz token was expired
    public TokensDTO reissueTokens(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰");
        }

        String userId = jwtUtil.extractUserId(refreshToken);
        String storedToken = tokenService.getRefreshToken(userId);

        if (!refreshToken.equals(storedToken)) {
            throw new IllegalArgumentException("토큰 불일치");
        }

        return issueTokens(userId);
    }

    // token-issue-logic
    private TokensDTO issueTokens(String userId) {
        String accessToken = jwtUtil.generateAccessToken(userId);
        String refreshToken = jwtUtil.generateRefreshToken(userId);

        tokenService.saveRefreshToken(userId, refreshToken); // ✅ DB 접근 (RefreshToken 저장)

        return new TokensDTO(accessToken, refreshToken);
    }


}
