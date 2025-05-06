package Bubble.bubblog.domain.category.entity;
import Bubble.bubblog.domain.category.entity.identifier.CategoryClosureId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "category_closure")
@IdClass(CategoryClosureId.class)
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class CategoryClosure {

    @Id
    @Column(name = "ancestor_id")
    private Long ancestorId;

    @Id
    @Column(name = "descendant_id")
    private Long descendantId;

    private int depth;

    private CategoryClosure(Long ancestorId, Long descendantId, int depth) {
        this.ancestorId = ancestorId;
        this.descendantId = descendantId;
        this.depth = depth;
    }

    public static CategoryClosure of(Long ancestorId, Long descendantId, int depth) {
        return new CategoryClosure(ancestorId, descendantId, depth);
    }
}