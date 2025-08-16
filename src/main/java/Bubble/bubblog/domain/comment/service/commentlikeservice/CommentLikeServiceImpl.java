package Bubble.bubblog.domain.comment.service.commentlikeservice;

import Bubble.bubblog.domain.comment.entity.Comment;
import Bubble.bubblog.domain.comment.entity.CommentLike;
import Bubble.bubblog.domain.comment.repository.CommentLikeRepository;
import Bubble.bubblog.domain.comment.repository.CommentRepository;
import Bubble.bubblog.domain.user.entity.User;
import Bubble.bubblog.domain.user.repository.UserRepository;
import Bubble.bubblog.global.exception.CustomException;
import Bubble.bubblog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentLikeServiceImpl implements CommentLikeService {
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public void toggleCommentLike(Long commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (comment.getIsDeleted()) {
            throw new CustomException(ErrorCode.CANNOT_LIKE_DELETED_COMMENT);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        commentLikeRepository.findByCommentAndUser(comment, user)
                .ifPresentOrElse(
                        // 이미 좋아요를 누른 경우
                        commentLike -> {
                            commentLikeRepository.delete(commentLike);
                            comment.decrementLikeCount();
                        },
                        // 좋아요를 누르지 않은 경우
                        () -> {
                            CommentLike newLike = CommentLike.createCommentLike(comment, user);
                            commentLikeRepository.save(newLike);
                            comment.incrementLikeCount();
                        }
                );
    }
}
