package Bubble.bubblog.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessState {
    SUCCESS(HttpStatus.OK.value(), "성공하였습니다.");

    private int code;
    private String message;
}
