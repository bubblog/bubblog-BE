package Bubble.bubblog.global.dto.swaggerResponse.info;

import Bubble.bubblog.domain.user.dto.infoRes.UserInfoDTO;
import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 정보 조회 성공 응답")
public class UserInfoSuccessResponse extends SuccessResponse<UserInfoDTO> {
    public UserInfoSuccessResponse(UserInfoDTO data) {
        super(200, "성공했습니다.", data);
    }
}
