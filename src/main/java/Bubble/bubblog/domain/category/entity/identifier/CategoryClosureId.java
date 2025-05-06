package Bubble.bubblog.domain.category.entity.identifier;

import java.io.Serializable;
import java.util.Objects;

// 복합키 식별자
// 엔티티에서 사용하기 위해 복합키를 정의해줘야 함
// equals와 hashCode를 반드시 오버라이드해야 JPA 내부에서 엔티티 비교, 관리가 올바르게 동작함
public class CategoryClosureId implements Serializable {
    private Long ancestorId;
    private Long descendantId;

    public CategoryClosureId() {}

    public CategoryClosureId(Long ancestorId, Long descendantId) {
        this.ancestorId = ancestorId;
        this.descendantId = descendantId;
    }

    // 복합키가 동일한지 깊은 비교를 통해 비교해줘야함
    // 일반 비교 시 참조값만 비교하여 동일해도 동일하지 않을 수 있음
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryClosureId)) return false;
        CategoryClosureId that = (CategoryClosureId) o;
        return Objects.equals(ancestorId, that.ancestorId) &&
                Objects.equals(descendantId, that.descendantId);
    }

    // 해시 기반 컬렉션(HashSet, HashMap) 및 JPA 내부 캐싱에서 동일성을 보장하기 위해 오버라이드
    @Override
    public int hashCode() {
        return Objects.hash(ancestorId, descendantId);
    }
}
