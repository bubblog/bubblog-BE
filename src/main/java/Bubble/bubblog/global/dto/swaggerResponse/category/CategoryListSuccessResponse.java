package Bubble.bubblog.global.dto.swaggerResponse.category;

import Bubble.bubblog.domain.category.dto.res.CategoryDTO;
import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "카테고리 목록 조회 성공 응답")
public class CategoryListSuccessResponse extends SuccessResponse<List<CategoryDTO>> {
    public CategoryListSuccessResponse(List<CategoryDTO> data) {
        super(200, "성공했습니다.", data);
    }
}