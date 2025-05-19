package Bubble.bubblog.domain.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "유저 정보 수정 DTO")
public class UserUpdateDTO {

    @Schema(description = "변경할 닉네임", example = "new_nickname")
    @Size(max = 30, message = "닉네임은 최대 30자까지 가능합니다.")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://bucketName.s3.amazonaws.com/images/@#@#@#_hanhwaeagles.jpg")
    private String profileImageUrl;
}
