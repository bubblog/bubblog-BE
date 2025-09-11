package Bubble.bubblog.global.dto.swaggerResponse.post;

import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

// 댓글 수 예시 스웨거에 표시
@Schema(description = "Long 타입 성공 응답")
public class LongSuccessResponse extends SuccessResponse<Long> {
    public LongSuccessResponse(Long data) {
        super(200, "성공했습니다.", data);
    }
}
