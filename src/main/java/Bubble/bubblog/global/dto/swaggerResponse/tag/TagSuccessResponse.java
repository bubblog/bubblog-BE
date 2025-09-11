package Bubble.bubblog.global.dto.swaggerResponse.tag;

import Bubble.bubblog.domain.tag.dto.res.TagResponseDTO;
import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "태그 단일 조회 성공 응답")
public class TagSuccessResponse extends SuccessResponse<TagResponseDTO> {
    public TagSuccessResponse(TagResponseDTO data) {
        super(200, "성공했습니다.", data);
    }
}