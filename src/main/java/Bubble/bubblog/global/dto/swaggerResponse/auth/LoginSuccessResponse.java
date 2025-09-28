package Bubble.bubblog.global.dto.swaggerResponse.auth;

import Bubble.bubblog.domain.user.dto.authRes.LoginResponseDTO;
import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 성공 응답")
public class LoginSuccessResponse extends SuccessResponse<LoginResponseDTO> {
    public LoginSuccessResponse(LoginResponseDTO data) {
        // 부모 클래스의 생성자를 호출하여 상태 코드, 메시지, 데이터를 설정.
        super(200, "성공했습니다.", data);
    }
}
