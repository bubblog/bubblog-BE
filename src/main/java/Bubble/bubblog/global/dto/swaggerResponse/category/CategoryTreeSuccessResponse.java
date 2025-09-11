package Bubble.bubblog.global.dto.swaggerResponse.category;

import Bubble.bubblog.domain.category.dto.res.CategoryTreeDTO;
import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "카테고리 트리 구조 조회 성공 응답")
public class CategoryTreeSuccessResponse extends SuccessResponse<List<CategoryTreeDTO>> {
    public CategoryTreeSuccessResponse(List<CategoryTreeDTO> data) {
        super(200, "성공했습니다.", data);
    }
}