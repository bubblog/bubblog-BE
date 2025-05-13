package Bubble.bubblog.domain.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 요청 DTO")
@Getter
@Setter
@NoArgsConstructor
public class SignupRequestDTO {

    @Schema(description = "이메일", example = "user@example.com")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @Schema(description = "비밀번호", example = "P@ssw0rd!")
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 설정해야 합니다.")
    private String password;

    @Schema(description = "닉네임", example = "홍길동")
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(max = 30, message = "닉네임은 최대 30자까지 가능합니다.")
    private String nickname;
}