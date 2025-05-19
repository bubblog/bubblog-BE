package Bubble.bubblog.domain.user.dto.infoRes;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserInfoDTO {
    private UUID userId;
    private String nickname;
    private String profileImageUrl;
}
