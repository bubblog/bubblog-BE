package Bubble.bubblog.domain.user.dto.res;

import lombok.Getter;

@Getter
public class AccessTokenDTO {
    private String accessToken;

    public AccessTokenDTO(String accessToken) {
        this.accessToken = accessToken;
    }
}