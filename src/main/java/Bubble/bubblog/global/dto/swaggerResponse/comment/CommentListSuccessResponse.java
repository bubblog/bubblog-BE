package Bubble.bubblog.global.dto.swaggerResponse.comment;

import Bubble.bubblog.domain.comment.dto.res.CommentResponseDTO;
import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "댓글 목록 조회 성공 응답")
public class CommentListSuccessResponse extends SuccessResponse<List<CommentResponseDTO>> {
    public CommentListSuccessResponse(List<CommentResponseDTO> data) {
        super(200, "성공했습니다.", data);
    }
}
