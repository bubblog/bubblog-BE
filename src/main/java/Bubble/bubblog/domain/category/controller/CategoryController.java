package Bubble.bubblog.domain.category.controller;

import Bubble.bubblog.domain.category.dto.req.CategoryCreateDTO;
import Bubble.bubblog.domain.category.dto.req.CategoryUpdateDTO;
import Bubble.bubblog.domain.category.dto.res.CategoryDTO;
import Bubble.bubblog.domain.category.dto.res.CategoryTreeDTO;
import Bubble.bubblog.domain.category.service.CategoryService;
import Bubble.bubblog.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Category", description = "카테고리 관련 API")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    @Operation(summary = "카테고리 생성", description = "새 카테고리를 생성합니다. (부모 ID는 선택)")
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryCreateDTO request,
                                                      @Parameter(hidden = true) @AuthenticationPrincipal UUID userId) {
        System.out.println("✅ userId: " + userId);

        CategoryDTO category = categoryService.createCategory(request.getName(), request.getParentId(), userId);
        return ResponseEntity.ok(new CategoryDTO(category.getId(), category.getName()));
    }

    @Operation(summary = "카테고리 수정", description = "카테고리 이름 또는 부모 카테고리를 변경합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCategory(@PathVariable Long id,
                                               @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
                                               @RequestBody CategoryUpdateDTO request) {
        categoryService.updateCategory(id, request.getName(), request.getNewParentId(), userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "카테고리 삭제", description = "카테고리를 삭제합니다. (하위 포함)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id,
                                               @Parameter(hidden = true) @AuthenticationPrincipal UUID userId) {
        categoryService.deleteCategory(id, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "모든 카테고리 조회", description = "유저의 모든 카테고리를 리스트로 조회합니다.")
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories(@Parameter(hidden = true) @AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(categoryService.getAllCategoriesAsDto(userId));
    }

    @Operation(summary = "카테고리 트리 조회", description = "유저의 모든 카테고리를 트리 구조로 조회합니다.")
    @GetMapping("/tree")
    public ResponseEntity<List<CategoryTreeDTO>> getAllCategoriesAsTree(@Parameter(hidden = true) @AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(categoryService.getAllCategoriesAsTree(userId));
    }

    @Operation(summary = "특정 카테고리 하위 트리 조회", description = "특정 카테고리의 하위 트리 구조를 조회합니다.")
    @GetMapping("/{id}/tree")
    public ResponseEntity<CategoryTreeDTO> getCategoryWithDescendants(@PathVariable Long id,
                                                                      @Parameter(hidden = true) @AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(categoryService.getCategoryWithDescendants(id, userId));
    }
}