package Bubble.bubblog.domain.post.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "블로그 게시글 작성 요청 DTO")
public class BlogPostRequestDTO {

    @Schema(description = "제목", example = "5월 12일 야구장 직관")
    @NotBlank(message = "제목은 필수 입력값입니다.")
    @Size(max = 100, message = "제목은 최대 100자까지 가능합니다.")
    private String title;

    @Schema(description = "내용", example = "오늘도 승요했다~!")
    @NotBlank(message = "내용은 필수 입력값입니다.")
    private String content;

    @Schema(description = "요약", example = "한화이글스 12연승")
    @Size(max = 255, message = "요약은 최대 255자까지 가능합니다.")
    private String summary;

    @Schema(description = "카테고리 ID", example = "1")
    @NotNull(message = "카테고리 ID는 필수 입력값입니다.")
    private Long categoryId;

    @Schema(description = "공개 여부", example = "true")
    private boolean publicVisible;

    @Schema(description = "썸네일 URL", example = "https://bucketName.s3.amazonaws.com/images/@#@#@#_hanhwaeagles.jpg")
    private String thumbnailUrl;
}