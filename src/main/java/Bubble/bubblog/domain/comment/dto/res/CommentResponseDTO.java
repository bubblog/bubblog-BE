package Bubble.bubblog.domain.comment.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CommentResponseDTO {
    private Long id;

    private String content;

    private boolean deleted;

    private String writerNickname;

    private String writerProfileImage;

    private Integer likeCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long parentId;   // 루트 : null, 자식 : parentId

    private Long replyCount;  // 자식은 null

}