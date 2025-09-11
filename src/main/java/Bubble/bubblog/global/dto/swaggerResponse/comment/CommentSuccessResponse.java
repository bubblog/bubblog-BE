package Bubble.bubblog.global.dto.swaggerResponse.comment;

import Bubble.bubblog.domain.comment.dto.res.CommentResponseDTO;
import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "댓글 생성 성공 응답")
public class CommentSuccessResponse extends SuccessResponse<CommentResponseDTO> {
    public CommentSuccessResponse(CommentResponseDTO data) {
        super(200, "성공했습니다.", data);
    }
}
