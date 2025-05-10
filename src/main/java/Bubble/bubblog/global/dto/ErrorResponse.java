package Bubble.bubblog.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "에러 응답")
@Getter
public class ErrorResponse<T> {
    @Schema(description = "성공 여부", example = "false")
    private boolean success = false;

    @Schema(description = "HTTP 상태 코드", example = "500")
    private int code;

    @Schema(description = "메시지", example = "실패했습니다.")
    private String message;

    @Schema(description = "에러 데이터")
    private T data;

    public ErrorResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ErrorResponse<T> of(int code, String message) {
        return new ErrorResponse<>(code, message, null);
    }

    public static <T> ErrorResponse<T> of(int code, String message, T data) {
        return new ErrorResponse<>(code, message, data);
    }
}