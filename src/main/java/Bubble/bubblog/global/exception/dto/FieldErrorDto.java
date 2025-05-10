package Bubble.bubblog.global.exception.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "필드 단위 검증 오류 정보")
@Getter
@AllArgsConstructor
public class FieldErrorDto {
    @Schema(description = "문제가 발생한 필드명", example = "username")
    private final String field;

    @Schema(description = "허용되지 않은 값", example = "null")
    private final Object rejectedValue;

    @Schema(description = "오류 사유", example = "필수 입력값입니다")
    private final String reason;
}