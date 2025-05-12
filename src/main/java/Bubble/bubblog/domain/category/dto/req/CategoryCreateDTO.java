package Bubble.bubblog.domain.category.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "카테고리 생성 요청 DTO")
public class CategoryCreateDTO {

    @Schema(description = "카테고리 이름", example = "백엔드")
    @NotBlank(message = "카테고리 이름은 필수 입력값입니다.")
    @Size(max = 50, message = "카테고리 이름은 최대 50자까지 가능합니다.")
    private String name;

    @Schema(description = "부모 카테고리 ID (선택)", example = "1")
    private Long parentId;  // 부모 ID (선택)
}