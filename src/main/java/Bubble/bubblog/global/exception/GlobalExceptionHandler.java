package Bubble.bubblog.global.exception;

import Bubble.bubblog.global.dto.ErrorResponse;
import Bubble.bubblog.global.exception.dto.FieldErrorDto;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import static Bubble.bubblog.global.exception.ErrorCode.*;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비밀번호 틀림
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse<Void> handleBadCredentials(BadCredentialsException ex) {
        return ErrorResponse.of(
                ErrorCode.UNAUTHORIZED.getCode(),
                "아이디 또는 비밀번호가 올바르지 않습니다."
        );
    }

    // 그 외 인증 실패
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse<Void> handleAuthenticationException(AuthenticationException ex) {
        return ErrorResponse.of(
                ErrorCode.UNAUTHORIZED.getCode(),
                "인증에 실패했습니다."
        );
    }

    // @Valid, @Validated 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse<List<FieldErrorDto>> handleValidationException(MethodArgumentNotValidException ex) {
        List<FieldErrorDto> details = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(fe -> new FieldErrorDto(
                        fe.getField(),
                        fe.getRejectedValue(),
                        fe.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        return ErrorResponse.of(
                INVALID_INPUT.getCode(),
                INVALID_INPUT.getMessage(),
                details
        );
    }

    // JSON 파싱 오류 (잘못된 형식의 요청 바디)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse<String> handleParsingException(HttpMessageNotReadableException ex) {
        return ErrorResponse.of(
                INVALID_INPUT.getCode(),
                INVALID_INPUT.getMessage()
        );
    }

    // 커스텀 예외
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse<Void>> handleCustomException(CustomException ex) {
        // ex.getCode() 는 ErrorCode 에 정의된 정수(예: 400, 404, 500 등)
        return ResponseEntity
                .status(HttpStatus.valueOf(ex.getCode()))
                .body(ErrorResponse.of(ex.getCode(), ex.getMessage()));
    }

    // 그 외 예기치 못한 예외
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse<Void> handleAllOther(Exception ex) {
        return ErrorResponse.of(
                INTERNAL_SERVER_ERROR.getCode(),
                INTERNAL_SERVER_ERROR.getMessage()
        );
    }
}