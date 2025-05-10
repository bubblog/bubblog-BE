package Bubble.bubblog.domain.category.controller;

import Bubble.bubblog.domain.category.dto.req.CategoryCreateDTO;
import Bubble.bubblog.domain.category.dto.req.CategoryUpdateDTO;
import Bubble.bubblog.domain.category.dto.res.CategoryDTO;
import Bubble.bubblog.domain.category.dto.res.CategoryTreeDTO;
import Bubble.bubblog.domain.category.service.CategoryService;
import Bubble.bubblog.global.dto.ErrorResponse;
import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Category", description = "카테고리 관련 API")
@RestController
@RequestMapping(value = "/api/categories", produces = "application/json")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 생성", description = "새 카테고리를 생성합니다. (부모 ID는 선택)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "대상을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public SuccessResponse<CategoryDTO> createCategory(
            @RequestBody CategoryCreateDTO request,
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId
    ) {
        CategoryDTO category = categoryService.createCategory(request.getName(), request.getParentId(), userId);
        return SuccessResponse.of(category);
    }

    @Operation(summary = "카테고리 수정", description = "카테고리 이름 또는 부모 카테고리를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "자기 자신을 부모로 지정할 수 없습니다. or 순환 참조가 발생했습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "대상을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public SuccessResponse<Void> updateCategory(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
            @RequestBody CategoryUpdateDTO request
    ) {
        categoryService.updateCategory(id, request.getName(), request.getNewParentId(), userId);
        return SuccessResponse.of();
    }

    @Operation(summary = "카테고리 삭제", description = "카테고리를 삭제합니다. (하위 포함)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "대상을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public SuccessResponse<Void> deleteCategory(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId
    ) {
        categoryService.deleteCategory(id, userId);
        return SuccessResponse.of();
    }

    @Operation(summary = "모든 카테고리 조회", description = "유저의 모든 카테고리를 리스트로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping
    public SuccessResponse<List<CategoryDTO>> getAllCategories(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId
    ) {
        return SuccessResponse.of(categoryService.getAllCategoriesAsDto(userId));
    }

    @Operation(summary = "카테고리 트리 조회", description = "유저의 모든 카테고리를 트리 구조로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/tree")
    public SuccessResponse<List<CategoryTreeDTO>> getAllCategoriesAsTree(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId
    ) {
        return SuccessResponse.of(categoryService.getAllCategoriesAsTree(userId));
    }

    @Operation(summary = "특정 카테고리 하위 트리 조회", description = "특정 카테고리의 하위 트리 구조를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "대상을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/tree")
    public SuccessResponse<CategoryTreeDTO> getCategoryWithDescendants(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId
    ) {
        return SuccessResponse.of(categoryService.getCategoryWithDescendants(id, userId));
    }
}