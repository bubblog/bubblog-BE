package Bubble.bubblog.global.dto.swaggerResponse.category;

import Bubble.bubblog.domain.category.dto.res.CategoryDTO;
import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 단일 작업 성공 응답")
public class CategorySuccessResponse extends SuccessResponse<CategoryDTO> {
    public CategorySuccessResponse(CategoryDTO data) {
        super(200, "성공했습니다.", data);
    }
}