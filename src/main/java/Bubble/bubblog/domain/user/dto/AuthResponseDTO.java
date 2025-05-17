package Bubble.bubblog.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class AuthResponseDTO {
    private String accessToken;
    private UUID userId;

    public AuthResponseDTO(String accessToken, UUID userId) {
        this.accessToken = accessToken;
        this.userId = userId;
    }
}