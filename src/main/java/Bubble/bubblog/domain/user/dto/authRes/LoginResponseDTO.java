package Bubble.bubblog.domain.user.dto.authRes;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class LoginResponseDTO {
    private String accessToken;
    private UUID userId;

    public LoginResponseDTO(String accessToken, UUID userId) {
        this.accessToken = accessToken;
        this.userId = userId;
    }
}