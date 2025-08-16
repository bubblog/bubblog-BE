package Bubble.bubblog.domain.comment.service.commentlikeservice;

import java.util.UUID;

public interface CommentLikeService {
    void toggleCommentLike(Long commentId, UUID userId);
}
