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
    DUPLICATE_NICKNAME(400, "이미 존재하는 닉네임입니다."),
    USER_NOT_FOUND(404, "존재하지 않는 회원입니다."),
    INVALID_PASSWORD(401, "비밀번호가 틀렸습니다."),
    INVALID_REFRESH_TOKEN(401, "유효하지 않거나 만료된 리프레시 토큰입니다."),
    REFRESH_TOKEN_MISMATCH(401, "저장된 리프레시 토큰과 일치하지 않습니다."),

    // 블로그 게시글
    POST_NOT_FOUND(404, "게시글을 찾을 수 없습니다."),
    UNAUTHORIZED_POST_ACCESS(403, "해당 게시글에 대한 권한이 없습니다."),
    UNAUTHORIZED_CATEGORY_ACCESS(403, "해당 카테고리에 대한 권한이 없습니다."),
    INVALID_SORT_FIELD(400, "잘못된 정렬 필드입니다."),

    // 카테고리
    CATEGORY_NOT_FOUND(404, "카테고리를 찾을 수 없습니다."),
    CATEGORY_CYCLE(400, "순환 참조가 발생했습니다."),
    CATEGORY_SELF_PARENT(400, "자기 자신을 부모로 지정할 수 없습니다."),

    // 페르소나
    PERSONA_NOT_FOUND(404, "말투를 찾을 수 없습니다."),
    UNAUTHORIZED_PERSONA_ACCESS(403, "해당 말투에 대한 권한이 없습니다."),

    // 태그
    DUPLICATE_TAG(400, "태그명이 중복입니다."),
    TAG_NOT_FOUND(404, "존재하지 않는 태그입니다."),

    // 댓글
    COMMENT_NOT_FOUND(404, "해당 댓글을 찾을 수 없습니다."),
    PARENT_COMMENT_NOT_FOUND(404, "부모 댓글을 찾을 수 없습니다."),
    INVALID_PARENT_FOR_POST(400, "해당 부모는 다른 게시글의 댓글입니다."),
    REPLY_TO_REPLY_NOT_ALLOWED(400, "대댓글에 대해 댓글을 작성할 수 없습니다."),
    REPLY_TO_DELETED_COMMENT_NOT_ALLOWED(400, "삭제된 댓글에 댓글을 작성할 수 없습니다."),
    NO_PERMISSION_TO_EDIT_COMMENT(403, "해당 댓글을 수정할 권한이 없습니다."),
    NO_PERMISSION_TO_DELETE_COMMENT(403, "해당 댓글을 삭제할 권한이 없습니다."),
    NOT_ROOT_COMMENT(400, "자식 댓글의 스레드를 조회할 수 없습니다. 루트 댓글의 스레드를 조회해주세요."),
    CANNOT_LIKE_DELETED_COMMENT(400, "삭제된 댓글에 좋아요를 누를 수 없습니다.");

    // S3

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}