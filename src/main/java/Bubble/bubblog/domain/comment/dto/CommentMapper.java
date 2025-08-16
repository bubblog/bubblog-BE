package Bubble.bubblog.domain.comment.dto;

import Bubble.bubblog.domain.comment.dto.res.CommentThreadResponseDTO;
import Bubble.bubblog.domain.comment.dto.res.CommentResponseDTO;
import Bubble.bubblog.domain.comment.entity.Comment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommentMapper {
    private static final String DELETED_PLACEHOLDER = "삭제된 댓글입니다.";

    /** 단일 댓글 */
    private CommentResponseDTO toDto(Comment comment, Long replyCount) {
        Long parentId = (comment.getParent() == null) ? null : comment.getParent().getId();

        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getIsDeleted() ? DELETED_PLACEHOLDER : comment.getContent())
                .deleted(comment.getIsDeleted())
                .writerNickname(comment.getUser().getNickname())
                .writerProfileImage(comment.getUser().getProfileImageUrl())
                .likeCount(comment.getLikeCount())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .parentId(parentId)
                .replyCount(replyCount)   // 기존 children.size() 방식에서 집계 값을 인자로 받는 것으로 리팩토링 => N+1문제 개선
                .build();
    }

    /** 루트 항목 : replyCount 집계값으로 주입 */
    public CommentResponseDTO toRoot(Comment root, long replyCount) {
        return toDto(root, replyCount);
    }

    /** 자식 항목 : replyCount를 null로 */
    public CommentResponseDTO toChild(Comment child) {
        return toDto(child, null);
    }


    /** 스레드 래퍼 : 계층 구조 응답 */
    public CommentThreadResponseDTO toThread(Comment root, long replyCount, List<Comment> children) {
        CommentResponseDTO rootDto = toRoot(root, replyCount);
        List<CommentResponseDTO> childrenDto = children.stream().map(this::toChild).toList();

        return CommentThreadResponseDTO.builder()
                .root(rootDto)
                .children(childrenDto)
                .build();
    }

}