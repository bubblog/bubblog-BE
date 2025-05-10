package Bubble.bubblog.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "성공 응답")
@Getter
public class SuccessResponse<T> {

    @Schema(description = "성공 여부", example = "true")
    private boolean success = true;

    @Schema(description = "HTTP 상태 코드", example = "200")
    private int code;

    @Schema(description = "메시지", example = "성공했습니다.")
    private String message;

    @Schema(description = "응답 데이터")
    private T data;

    public SuccessResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> SuccessResponse<T> of(T data) {
        return new SuccessResponse<>(SuccessState.SUCCESS.getCode(), SuccessState.SUCCESS.getMessage(), data);
    }

    public static <T> SuccessResponse<T> of() {
        return new SuccessResponse<>(SuccessState.SUCCESS.getCode(), SuccessState.SUCCESS.getMessage(), null);
    }

    public static <T> SuccessResponse<T> of(SuccessState state, T data) {
        return new SuccessResponse<>(state.getCode(), state.getMessage(), data);
    }
}