package Bubble.bubblog.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReissueRequestDTO {
    private String refreshToken;
}
