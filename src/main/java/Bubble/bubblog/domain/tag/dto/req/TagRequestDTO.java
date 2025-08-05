package Bubble.bubblog.domain.tag.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "챗봇 말투 생성 요청 DTO")
@NoArgsConstructor   // 기본 생성자 자동 생성 (Jackson이 JSON을 자바 객체로 역직렬화 할 때 기본 생성자 필요)
public class TagRequestDTO {
    @Schema(description = "태그 이름", example = "Spring")
    @NotBlank(message = "태그명은 필수 입력값입니다.")
    @Size(max = 50, message = "태그명은 최대 50자까지 가능합니다.")
    private String name;
}