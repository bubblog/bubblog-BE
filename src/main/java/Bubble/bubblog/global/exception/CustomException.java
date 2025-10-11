package Bubble.bubblog.global.exception;

public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause); // 원인 예외(cause)를 함께 넘겨줍니다.
        this.errorCode = errorCode;
    }

    public int getCode() {
        return errorCode.getCode();
    }

    public String getMessage() {
        return errorCode.getMessage();
    }
}