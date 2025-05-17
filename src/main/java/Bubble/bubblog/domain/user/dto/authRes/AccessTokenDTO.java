package Bubble.bubblog.domain.user.dto.authRes;

import lombok.Getter;

@Getter
public class AccessTokenDTO {
    private String accessToken;

    public AccessTokenDTO(String accessToken) {
        this.accessToken = accessToken;
    }
}