package Bubble.bubblog.domain.category.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "카테고리 수정 요청 DTO")
public class CategoryUpdateDTO {

    @Schema(description = "변경할 카테고리 이름", example = "프론트엔드")
    @Size(max = 50, message = "카테고리 이름은 최대 50자까지 가능합니다.")
    private String name;       // 변경할 이름

    @Schema(description = "변경할 부모 카테고리 ID", example = "3")
    private Long newParentId;  // 변경할 부모 ID
}
