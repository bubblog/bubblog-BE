package Bubble.bubblog.domain.comment.repository;

import Bubble.bubblog.domain.comment.entity.Comment;
import Bubble.bubblog.domain.comment.entity.CommentLike;
import Bubble.bubblog.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);
}
