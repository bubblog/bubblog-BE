package Bubble.bubblog.domain.post.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "S3 업로드용 프리사인드 URL 발급 DTO")
public class S3UploadRequestDTO {

    @Schema(description = "객체 URL", example = "https://bucketName.s3.amazonaws.com/images/@#@#@#_hanhwaeagles.jpg")
    @NotBlank(message = "파일 이름은 필수입니다.")
    private String fileName;

    @Schema(description = "콘텐츠 타입", example = "image/png")
    @NotBlank(message = "콘텐츠 타입은 필수입니다.")
    private String contentType;
}

