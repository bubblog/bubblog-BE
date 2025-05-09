package Bubble.bubblog.domain.post.repository;

import Bubble.bubblog.domain.post.entity.BlogPost;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findAllByPublicVisibleTrue();

    @EntityGraph(attributePaths = {"user"}) // user 정보를 미리 JOIN해서 가져옴
    List<BlogPost> findAllByUserId(UUID userId);

    @EntityGraph(attributePaths = {"user"}) // 공개 글만 가져오되, user까지 같이
    List<BlogPost> findAllByUserIdAndPublicVisibleTrue(UUID userId);
}
