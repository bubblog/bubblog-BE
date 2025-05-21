package Bubble.bubblog.domain.user.dto.authRes;

import lombok.Getter;

import java.util.UUID;

@Getter
public class TokensDTO {
    private final String accessToken;
    private final String refreshToken;
    private final UUID userId;

    public TokensDTO(String accessToken, String refreshToken, UUID userId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
}
