package Bubble.bubblog.domain.comment.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor   // 기본 생성자는 Jackson 역직렬화를 위해
public class CreateCommentDTO {
    @NotBlank(message = "댓글은 필수입니다")
    @Size(min = 1, max = 1000, message = "댓글은 최대 1000자까지 가능합니다")
    @Schema(description = "댓글 내용", example = "오늘 경기 너무 재밌었어요!")
    private String content;

    @Schema(description = "부모 ID", example = "null 또는 ID")
    private Long parentId; // 대댓글용 (null이면 최상위 댓글)
}