package Bubble.bubblog.domain.user.service;

import Bubble.bubblog.domain.user.dto.req.LoginRequestDTO;
import Bubble.bubblog.domain.user.dto.req.SignupRequestDTO;
import Bubble.bubblog.domain.user.dto.res.TokensDTO;
import Bubble.bubblog.domain.user.entity.User;
import Bubble.bubblog.domain.user.repository.UserRepository;
import Bubble.bubblog.global.exception.CustomException;
import Bubble.bubblog.global.exception.ErrorCode;
import Bubble.bubblog.global.service.TokenService;
import Bubble.bubblog.global.util.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    // signup
    @Transactional
    public void signup(SignupRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.from(request, passwordEncoder);
        userRepository.save(user);
    }

    // login
    public User login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        return user;
    }

    // logout
    public void logout(UUID userId) {
        tokenService.deleteRefreshToken(userId);
    }

    // reissue tokens cuz token was expired
    public TokensDTO reissueTokens(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        UUID userId = jwtUtil.extractUserId(refreshToken);
        String storedToken = tokenService.getRefreshToken(userId);

        if (!refreshToken.equals(storedToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        return issueTokens(userId);
    }

    // token-issue-logic
    public TokensDTO issueTokens(UUID userId) {
        String accessToken = jwtUtil.generateAccessToken(userId);
        String refreshToken = jwtUtil.generateRefreshToken(userId);

        tokenService.saveRefreshToken(userId, refreshToken);

        return new TokensDTO(accessToken, refreshToken);
    }
}
