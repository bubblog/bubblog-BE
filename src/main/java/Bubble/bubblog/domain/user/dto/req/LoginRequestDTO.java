package Bubble.bubblog.domain.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "로그인 요청 DTO")
public class LoginRequestDTO {

    @Schema(description = "이메일", example = "user@example.com")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Schema(description = "비밀번호", example = "P@ssw0rd!")
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
}
