package Bubble.bubblog.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignupRequestDTO {
    private String email;
    private String password;
    private String nickname;
}
