package Bubble.bubblog.domain.user.service;

import Bubble.bubblog.domain.user.dto.authRes.TokensDTO;
import Bubble.bubblog.domain.user.dto.infoRes.UserInfoDTO;
import Bubble.bubblog.domain.user.dto.req.LoginRequestDTO;
import Bubble.bubblog.domain.user.dto.req.SignupRequestDTO;
import Bubble.bubblog.domain.user.dto.req.UserUpdateDTO;
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

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
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

    // user 정보 조회
    public UserInfoDTO getUserInfo(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new UserInfoDTO(
                user.getId(),
                user.getNickname(),
                user.getProfileImageUrl()
        );
    }

    // user 정보 수정
    @Transactional
    public void updateUser(UUID userId, UserUpdateDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 닉네임 수정
        String newNickname = request.getNickname();
        if (newNickname != null && !newNickname.equals(user.getNickname())) {
            if (userRepository.existsByNickname(newNickname)) {
                throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
            }
            user.updateNickname(newNickname);
        }

        // 프로필 이미지 수정 (null이면 이미지 제거)
        user.updateProfileImageUrl(request.getProfileImageUrl());
    }

}
