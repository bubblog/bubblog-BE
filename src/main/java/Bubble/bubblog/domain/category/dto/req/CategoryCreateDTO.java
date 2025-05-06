package Bubble.bubblog.domain.category.dto.req;

import lombok.Getter;

@Getter
public class CategoryCreateDTO {

    private String name;
    private Long parentId;  // 부모 ID (선택)
}