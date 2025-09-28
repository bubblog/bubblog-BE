package Bubble.bubblog.global.dto.swaggerResponse.tag;

import Bubble.bubblog.domain.tag.dto.res.TagResponseDTO;
import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "태그 목록 조회 성공 응답")
public class TagListSuccessResponse extends SuccessResponse<List<TagResponseDTO>> {
    public TagListSuccessResponse(List<TagResponseDTO> data) {
        super(200, "성공했습니다.", data);
    }
}