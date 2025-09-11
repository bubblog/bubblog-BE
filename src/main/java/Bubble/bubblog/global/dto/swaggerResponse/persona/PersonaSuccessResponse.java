package Bubble.bubblog.global.dto.swaggerResponse.persona;

import Bubble.bubblog.domain.chatbot.dto.res.PersonaResponseDTO;
import Bubble.bubblog.global.dto.SuccessResponse;

public class PersonaSuccessResponse extends SuccessResponse<PersonaResponseDTO> {
    public PersonaSuccessResponse(PersonaResponseDTO data) {
        super(200, "성공했습니다.", data);
    }
}
