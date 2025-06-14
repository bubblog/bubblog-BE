package Bubble.bubblog.domain.post.repository;

import Bubble.bubblog.domain.post.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long>, BlogPostRepositoryCustom {

    @EntityGraph(attributePaths = {"user"})
    @Query("""
        SELECT bp
          FROM BlogPost bp
         WHERE bp.user.id = :userId
           AND bp.category.id IN :categoryIds
    """)
    Page<BlogPost> findAllByUserIdAndCategory(
            UUID userId,
            List<Long> categoryIds,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"user"})
    @Query("""
        SELECT bp
          FROM BlogPost bp
         WHERE bp.user.id = :userId
           AND bp.category.id IN :categoryIds
           AND bp.publicVisible = true
    """)
    Page<BlogPost> findAllByUserIdAndCategoryIdInAndPublicVisibleTrue(
            UUID userId,
            List<Long> categoryIds,
            Pageable pageable
    );
}
