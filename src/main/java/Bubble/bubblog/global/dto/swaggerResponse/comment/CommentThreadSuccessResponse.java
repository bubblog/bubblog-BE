package Bubble.bubblog.global.dto.swaggerResponse.comment;

import Bubble.bubblog.domain.comment.dto.res.CommentThreadResponseDTO;
import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "댓글 스레드 조회 성공 응답")
public class CommentThreadSuccessResponse extends SuccessResponse<CommentThreadResponseDTO> {
    public CommentThreadSuccessResponse(CommentThreadResponseDTO data) {
        super(200, "성공했습니다.", data);
    }
}