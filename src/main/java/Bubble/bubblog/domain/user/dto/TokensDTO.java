package Bubble.bubblog.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokensDTO {
    private String accessToken;
    private String refreshToken;
}
