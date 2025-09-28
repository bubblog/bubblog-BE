package Bubble.bubblog.global.dto.swaggerResponse.info;

import Bubble.bubblog.domain.post.dto.res.UserPostsResponseDTO;
import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 게시글 목록 조회 성공 응답")
public class UserPostsSuccessResponse extends SuccessResponse<UserPostsResponseDTO> {
    public UserPostsSuccessResponse(UserPostsResponseDTO data) {
        super(200, "성공했습니다.", data);
    }
}