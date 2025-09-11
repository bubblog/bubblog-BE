package Bubble.bubblog.global.dto.swaggerResponse.comment;

import Bubble.bubblog.domain.comment.dto.res.CommentResponseDTO;
import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

@Schema(description = "댓글 목록 조회 성공 응답 (페이징)")
public class CommentPageSuccessResponse extends SuccessResponse<Page<CommentResponseDTO>> {
    public CommentPageSuccessResponse(Page<CommentResponseDTO> data) {
        super(200, "성공했습니다.", data);
    }
}
