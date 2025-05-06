package Bubble.bubblog.domain.category.dto.res;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CategoryTreeDTO {
    private final Long id;
    private final String name;
    private final List<CategoryTreeDTO> children = new ArrayList<>();
    @Getter
    private boolean root = false;

    public CategoryTreeDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addChild(CategoryTreeDTO child) {
        this.children.add(child);
    }

    public void setAsRoot() {
        this.root = true;
    }

}