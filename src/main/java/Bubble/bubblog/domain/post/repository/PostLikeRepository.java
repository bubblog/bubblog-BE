package Bubble.bubblog.domain.post.repository;

import Bubble.bubblog.domain.post.entity.BlogPost;
import Bubble.bubblog.domain.post.entity.PostLike;
import Bubble.bubblog.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByUserAndPost(User user, BlogPost post);

    @EntityGraph(attributePaths = {"user"})
    @Query("""
        SELECT p FROM BlogPost p
        WHERE p IN (
            SELECT pl.post FROM PostLike pl WHERE pl.user = :user
        )
    """)
    Page<BlogPost> findLikedPostsByUser(@Param("user") User user, Pageable pageable);
}
