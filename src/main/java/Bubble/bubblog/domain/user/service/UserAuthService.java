package Bubble.bubblog.domain.user.service;

import Bubble.bubblog.domain.user.dto.authRes.TokensDTO;
import Bubble.bubblog.domain.user.dto.req.LoginRequestDTO;
import Bubble.bubblog.domain.user.dto.req.SignupRequestDTO;
import Bubble.bubblog.domain.user.entity.User;

import java.util.UUID;

public interface UserAuthService {
    // 사용자 인증 서비스
    void signup(SignupRequestDTO request);
    User login(LoginRequestDTO request);
    void logout(UUID userId);
    TokensDTO reissueTokens(String refreshToken);
    TokensDTO issueTokens(UUID userId);
}