package Bubble.bubblog.domain.category.dto.req;

import lombok.Getter;

@Getter
public class CategoryUpdateDTO {
    private String name;       // 변경할 이름
    private Long newParentId;  // 변경할 부모 ID
}
