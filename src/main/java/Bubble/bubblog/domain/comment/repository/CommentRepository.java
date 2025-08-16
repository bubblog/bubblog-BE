package Bubble.bubblog.domain.comment.repository;

import Bubble.bubblog.domain.comment.dto.ReplyCount;
import Bubble.bubblog.domain.comment.entity.Comment;
import Bubble.bubblog.domain.post.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // User와 같이 fetch join해서 댓글 단건을 불러옴.
    @Query("""
        SELECT c
        FROM Comment c
        JOIN FETCH c.user
        WHERE c.id = :id
    """)
    Optional<Comment> findByIdWithUser(@Param("id") Long id);

    // replyCount 집계 쿼리 : 루트ID 묶음에 대한 자식 개수 -> 별도의 집계 쿼리를 구성하지 않으면 루트 개수만큼 쿼리를 날림(각 루트마다 children에 접근하므로)
    @Query("""
         SELECT c.parent.id AS rootId, COUNT(c.id) AS cnt
         FROM Comment c
         WHERE c.parent.id IN :rootIds
         GROUP BY c.parent.id
    """)
    List<ReplyCount> countByParentIds(@Param("rootIds") Collection<Long> rootIds);

    // 루트 목록 조회 : N+1문제 방지를 위해 root 조회 시 user까지 한방에 조회
    @Query("""
        SELECT c FROM Comment c
        JOIN FETCH c.user
        WHERE c.post.id = :postId
          AND c.parent IS NULL
        ORDER BY c.createdAt ASC
    """)
    List<Comment> findRootCommentsByPostId(@Param("postId") Long postId);

    // 스레드를 위해서 조회 : 특정 루트의 자식들 및 user를 한방에 가져옴
    @Query("""
        SELECT c FROM Comment c
        JOIN FETCH c.user
        WHERE c.parent.id = :rootId
        ORDER BY c.createdAt ASC
    """)
    List<Comment> findChildrenByRootIdForThread(@Param("rootId") Long rootId);

    // 인자로 받은 id가 부모 id인, 즉 그 자식들 수 카운트
    Long countByParentId(Long commentId);

    // 특정 루트 댓글의 자식들을 페이징하여 조회 및 user를 한방에 가져옴
    @Query(value = """
        SELECT c FROM Comment c
        JOIN FETCH c.user
        WHERE c.parent.id = :rootId
        ORDER BY c.createdAt ASC
    """, countQuery = "SELECT COUNT(c) FROM Comment c WHERE c.parent.id = :rootId")
    Page<Comment> findChildrenByParentId(@Param("rootId") Long rootId, Pageable pageable);

    @Query("select count(c) from Comment c where c.post.id = :postId")
    Long countByPostId(@Param("postId") Long postId);

    // 내가 작성한 댓글들 목록 조회
    @Query(value = """
        SELECT c FROM Comment c
        JOIN FETCH c.user
        WHERE c.user.id = :userId AND c.isDeleted = false
        ORDER BY c.createdAt DESC
    """, countQuery = "SELECT COUNT(c) FROM Comment c WHERE c.user.id = :userId AND c.isDeleted = false")
    Page<Comment> findByUserId(UUID userId, Pageable pageable);

    // 내가 댓글 단 게시글 목록 중복 없이 가져오기 + is_deleted = false 조건을 추가하여 삭제된 댓글을 조회에서 제외
    @Query(value = """
        SELECT DISTINCT c.post FROM Comment c
        JOIN FETCH c.post.user
        WHERE c.user.id = :userId AND c.isDeleted = false
        ORDER BY c.post.createdAt DESC
    """, countQuery = "SELECT COUNT(DISTINCT c.post.id) FROM Comment c WHERE c.user.id = :userId AND c.isDeleted = false")
    Page<BlogPost> findCommentedPostsByUserId(UUID userId, Pageable pageable);
}
