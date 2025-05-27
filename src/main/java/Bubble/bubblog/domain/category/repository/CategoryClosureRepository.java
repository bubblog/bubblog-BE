package Bubble.bubblog.domain.category.repository;

import Bubble.bubblog.domain.category.entity.CategoryClosure;
import Bubble.bubblog.domain.category.entity.identifier.CategoryClosureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CategoryClosureRepository extends JpaRepository<CategoryClosure, CategoryClosureId> {

    // 조상 카테고리로 모든 후손 ID (depth 무관)
    @Query("SELECT c.descendantId FROM CategoryClosure c WHERE c.ancestorId = :ancestorId")
    List<Long> findDescendantIds(Long ancestorId);

    // 자신의 직접 부모 ID (depth = 1)
    @Query("SELECT c.ancestorId FROM CategoryClosure c WHERE c.descendantId = :descendantId AND c.depth = 1")
    Optional<Long> findDirectParentId(Long descendantId);

    // descendantId인 모든 관계 즉, 자신이 하위인 모든 조회
    List<CategoryClosure> findAllByDescendantId(Long descendantId);

    // DELETE derived (모든 deleteByXxx는 @Modifying 필요)
    // 자신이 하위인 모든 관계 제거
    @Modifying
    void deleteByDescendantId(Long descendantId);

    // 카테고리 리스트의 모든 자신이 하위인 관계를(자신, 자신, 0 제외) 제거
    @Modifying
    @Query("""
      DELETE FROM CategoryClosure cc
      WHERE cc.descendantId IN :subtreeIds
        AND cc.ancestorId NOT IN :subtreeIds
    """)
    void deleteExternalAncestors(List<Long> subtreeIds);

    // 원래 깊이 정보 조회
    @Query("SELECT c FROM CategoryClosure c WHERE c.ancestorId = :ancestorId AND c.descendantId IN :subtreeIds")
    List<CategoryClosure> findDepthsByAncestorAndDescendants(Long ancestorId, List<Long> subtreeIds);

    // 하위 카테고리 아이디로 상위 카테고리를 순서대로 리스트로 반환
    @Query("""
        SELECT c.name
          FROM CategoryClosure cc
          JOIN Category c ON c.id = cc.ancestorId
         WHERE cc.descendantId = :descendantId
         ORDER BY cc.depth DESC
    """)
    List<String> findAncestorNamesByDescendantId(Long descendantId);
}