package Bubble.bubblog.global.dto.swaggerResponse.persona;

import Bubble.bubblog.domain.chatbot.dto.res.PersonaResponseDTO;
import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "페르소나 목록 조회 성공 응답")
public class PersonaListSuccessResponse extends SuccessResponse<List<PersonaResponseDTO>> {
    public PersonaListSuccessResponse(List<PersonaResponseDTO> data) {
        super(200, "성공했습니다.", data);
    }
}
