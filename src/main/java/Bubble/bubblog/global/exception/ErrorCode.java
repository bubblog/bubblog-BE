package Bubble.bubblog.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 전체 에러
    INVALID_INPUT(400, "잘못된 요청입니다."),
    ENTITY_NOT_FOUND(404, "대상을 찾을 수 없습니다."),
    UNAUTHORIZED(401, "권한이 없습니다."),
    FORBIDDEN(403, "접근이 금지되었습니다."),
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),

    // 도메인별 에러

    //카테고리
    CATEGORY_NOT_FOUND(404, "카테고리를 찾을 수 없습니다."),
    CATEGORY_CYCLE(400, "순환 참조가 발생했습니다."),
    CATEGORY_SELF_PARENT(400, "자기 자신을 부모로 지정할 수 없습니다.");


    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}