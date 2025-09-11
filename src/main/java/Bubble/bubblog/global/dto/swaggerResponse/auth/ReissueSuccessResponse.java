package Bubble.bubblog.global.dto.swaggerResponse.auth;

import Bubble.bubblog.domain.user.dto.authRes.ReissueResponseDTO;
import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 재발급 성공 응답")
public class ReissueSuccessResponse extends SuccessResponse<ReissueResponseDTO> {
    public ReissueSuccessResponse(ReissueResponseDTO data) {
        super(200, "성공했습니다.", data);
    }
}
