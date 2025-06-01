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
    // 유저
    DUPLICATE_EMAIL(400, "이미 존재하는 이메일입니다."),
    DUPLICATE_NICKNAME(400, "이미 존재하는 이메일입니다."),
    USER_NOT_FOUND(404, "존재하지 않는 이메일입니다."),
    INVALID_PASSWORD(401, "비밀번호가 틀렸습니다."),
    INVALID_REFRESH_TOKEN(401, "유효하지 않거나 만료된 리프레시 토큰입니다."),
    REFRESH_TOKEN_MISMATCH(401, "저장된 리프레시 토큰과 일치하지 않습니다."),

    // 블로그 게시글
    POST_NOT_FOUND(404, "게시글을 찾을 수 없습니다."),
    UNAUTHORIZED_POST_ACCESS(403, "해당 게시글에 대한 권한이 없습니다."),
    UNAUTHORIZED_CATEGORY_ACCESS(403, "해당 카테고리에 대한 권한이 없습니다."),

    // 카테고리
    CATEGORY_NOT_FOUND(404, "카테고리를 찾을 수 없습니다."),
    CATEGORY_CYCLE(400, "순환 참조가 발생했습니다."),
    CATEGORY_SELF_PARENT(400, "자기 자신을 부모로 지정할 수 없습니다."),

    //
    PERSONA_NOT_FOUND(404, "말투를 찾을 수 없습니다."),
    UNAUTHORIZED_PERSONA_ACCESS(403, "해당 말투에 대한 권한이 없습니다.");

    // S3

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}