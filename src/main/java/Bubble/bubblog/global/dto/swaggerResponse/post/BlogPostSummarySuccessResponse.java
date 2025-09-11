package Bubble.bubblog.global.dto.swaggerResponse.post;

import Bubble.bubblog.domain.post.dto.res.BlogPostSummaryDTO;
import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

@Schema(description = "게시글 요약 목록 조회 성공 응답 (페이징)")
public class BlogPostSummarySuccessResponse extends SuccessResponse<Page<BlogPostSummaryDTO>> {
    public BlogPostSummarySuccessResponse(Page<BlogPostSummaryDTO> data) {
        super(200, "성공했습니다.", data);
    }
}