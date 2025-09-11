package Bubble.bubblog.global.dto.swaggerResponse.post;

import Bubble.bubblog.domain.post.dto.res.BlogPostDetailDTO;
import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 상세 정보 조회 성공 응답")
public class BlogPostDetailSuccessResponse extends SuccessResponse<BlogPostDetailDTO> {
    public BlogPostDetailSuccessResponse(BlogPostDetailDTO data) {
        super(200, "성공했습니다.", data);
    }
}
