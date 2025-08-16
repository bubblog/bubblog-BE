package Bubble.bubblog.domain.user.service;

import Bubble.bubblog.domain.user.dto.authRes.TokensDTO;
import Bubble.bubblog.domain.user.dto.req.LoginRequestDTO;
import Bubble.bubblog.domain.user.dto.req.SignupRequestDTO;
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
public class UserAuthServiceImpl implements UserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    // 회원가입
    @Override
    @Transactional
    public void signup(SignupRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        User user = User.from(request, passwordEncoder);
        userRepository.save(user);
    }

    // 로그인
    @Override
    @Transactional(readOnly = true)
    public User login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        return user;
    }

    // 로그아웃
    @Override
    @Transactional
    public void logout(UUID userId) {
        tokenService.deleteRefreshToken(userId);
    }

    // 토큰 재발행
    @Override
    @Transactional
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

    // 토큰 발행 함수
    @Override
    @Transactional
    public TokensDTO issueTokens(UUID userId) {
        String accessToken = jwtUtil.generateAccessToken(userId);
        String refreshToken = jwtUtil.generateRefreshToken(userId);

        tokenService.saveRefreshToken(userId, refreshToken);

        return new TokensDTO(accessToken, refreshToken, userId);
    }

}
