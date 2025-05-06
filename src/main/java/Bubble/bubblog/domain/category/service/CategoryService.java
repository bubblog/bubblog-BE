package Bubble.bubblog.domain.category.service;

import Bubble.bubblog.domain.category.dto.res.CategoryDTO;
import Bubble.bubblog.domain.category.dto.res.CategoryTreeDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryDTO createCategory(String name, Long parentId, UUID userId);

    void updateCategory(Long categoryId, String newName, Long newParentId, UUID userId);

    void deleteCategory(Long categoryId, UUID userId);

    List<CategoryDTO> getAllCategoriesAsDto(UUID userId);

    List<CategoryTreeDTO> getAllCategoriesAsTree(UUID userId);

    CategoryTreeDTO getCategoryWithDescendants(Long categoryId, UUID userId);
}
