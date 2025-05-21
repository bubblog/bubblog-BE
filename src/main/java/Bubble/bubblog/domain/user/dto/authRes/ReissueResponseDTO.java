package Bubble.bubblog.domain.user.dto.authRes;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ReissueResponseDTO {
    private String accessToken;
    private UUID userId;

    public ReissueResponseDTO(String accessToken, UUID userId) {
        this.accessToken = accessToken;
        this.userId = userId;
    }
}