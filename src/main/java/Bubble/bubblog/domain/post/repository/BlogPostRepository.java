package Bubble.bubblog.domain.post.repository;

import Bubble.bubblog.domain.post.entity.BlogPost;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
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

    // 게시글 상세 조회 JPA
    @EntityGraph(attributePaths = {"postTags", "postTags.tag", "category", "user"})
    @Query("SELECT bp FROM BlogPost bp WHERE bp.id = :id")
    Optional<BlogPost> findDetailById(Long id);

    // 태그ID 기반 게시글 목록 조회
    @EntityGraph(attributePaths = {"category", "user"})
    @Query("SELECT bp FROM BlogPost bp JOIN bp.postTags pt WHERE pt.tag.id = :tagId AND bp.publicVisible = true")
    Page<BlogPost> findPublicPostsByTagId(@Param("tagId") Long tagId, Pageable pageable);

}
