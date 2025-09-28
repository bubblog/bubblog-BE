package Bubble.bubblog.global.dto.swaggerResponse.post;

import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

// 좋아요 컨트롤러 관련 스웨거 응답 DTO
@Schema(description = "Boolean 타입 성공 응답")
public class BooleanSuccessResponse extends SuccessResponse<Boolean> {
    public BooleanSuccessResponse(Boolean data) {
        super(200, "성공했습니다.", data);
    }
}