package Bubble.bubblog.domain.chatbot.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "챗봇 말투 생성 요청 DTO")
public class PersonaRequestDTO {

    @Schema(description = "말투 이름", example = "뼛속까지 개발자")
    @NotBlank(message = "말투 명은 필수 입력값입니다.")
    @Size(max = 100, message = "말투 명은 최대 100자까지 가능합니다.")
    private String name;

    @Schema(description = "말투에 대한 설명", example = "진정한 개발에 미친 사람처럼 보이게 답변에서 문장 끝날 때마다 세미클론을 붙여줘")
    @NotBlank(message = "설명은 필수 입력값입니다.")
    private String description;

}

