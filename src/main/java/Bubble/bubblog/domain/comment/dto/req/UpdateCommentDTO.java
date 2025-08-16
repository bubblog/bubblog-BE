package Bubble.bubblog.domain.comment.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateCommentDTO {
    @NotBlank(message = "댓글은 필수입니다")
    @Size(min = 1, max = 1000, message = "댓글은 최대 1000자까지 가능합니다")
    @Schema(description = "댓글 내용", example = "댓글 수정했어요!")
    private String content;
}
