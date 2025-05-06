package Bubble.bubblog.domain.category.dto.res;

import lombok.Getter;

@Getter
public class CategoryDTO{

    private final Long id;
    private final String name;

    public CategoryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}