package Bubble.bubblog.domain.post.controller;

import Bubble.bubblog.domain.post.dto.req.BlogPostRequestDTO;
import Bubble.bubblog.domain.post.dto.res.BlogPostDetailDTO;
import Bubble.bubblog.domain.post.dto.res.BlogPostSummaryDTO;
import Bubble.bubblog.domain.post.dto.res.UserPostsResponseDTO;
import Bubble.bubblog.domain.post.service.BlogPostService;
import Bubble.bubblog.global.dto.ErrorResponse;
import Bubble.bubblog.global.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

// 게시글 관련 컨트롤러
@Tag(name = "Blog Post", description = "블로그 게시글 관련 API")
@RestController
@RequestMapping(value = "/api/posts", produces = "application/json")
@RequiredArgsConstructor
public class BlogPostController {

    private final BlogPostService blogPostService;

    // 게시글 생성
    @Operation(summary = "게시글 생성", description = "사용자가 새 게시글을 작성합니다.", security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 생성 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "입력값이 유효하지 않음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "카테고리 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public SuccessResponse<BlogPostDetailDTO> createPost(@Valid @RequestBody BlogPostRequestDTO request,
                                                         @Parameter(hidden = true) @AuthenticationPrincipal UUID userId) {
        BlogPostDetailDTO dto = blogPostService.createPost(request, userId);
        return SuccessResponse.of(dto);
    }

    // 특정 게시글 조회
    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{postId}")
    public SuccessResponse<BlogPostDetailDTO> getPost(@PathVariable Long postId) {  // @PathVariable은 URL 경로에 포함된 값을 컨트롤러 메서드의 파라미터로 바인딩해주는 역할을 함
        return SuccessResponse.of(blogPostService.getPost(postId));
    }

    // 전체 게시글 조회
    @Operation(
            summary = "공개 게시글 전체 조회",
            description = """
            전체 공개된 게시글을 페이지 단위로 조회합니다.
        
            - `keyword` : 게시글의 제목, 내용, 요약에 대해 부분 일치 검색을 수행합니다.
            - `sort` : 정렬 기준을 지정할 수 있습니다.
                - 예: `sort=likeCount,DESC` (좋아요 많은 순)
                - 예: `sort=viewCount,DESC` (조회수 많은 순)
                - 예: `sort=createdAt,DESC` (최신순)
            - 정렬 기준은 여러 개를 조합할 수 있으며, 우선순위는 파라미터 순서대로 적용됩니다.
                - 예: `sort=likeCount,DESC&sort=createdAt,DESC` → 좋아요 순 → 최신순
        
            [요청 예시]
            GET /api/posts?keyword=spring&sort=likeCount,DESC&page=0&size=6
        
            기본값:
            - page=0
            - size=6
            - sort=createdAt,DESC
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping
    public SuccessResponse<Page<BlogPostSummaryDTO>> getAllPosts(
            @RequestParam(required = false) String keyword,
            @ParameterObject
            @PageableDefault(size = 6, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<BlogPostSummaryDTO> posts = blogPostService.getAllPosts(keyword, pageable);
        return SuccessResponse.of(posts);
    }

    // 특정 사용자의 게시글 목록을 조회
    @Operation(
            summary = "사용자 게시글 조회",
            description = """
            특정 사용자의 게시글을 조회합니다.
            - `categoryId` : 선택적으로 하위 카테고리를 포함한 특정 카테고리의 게시글만 조회할 수 있습니다.
            - `sort` : 정렬 기준을 지정할 수 있습니다.
                - 예: `sort=likeCount,DESC` (좋아요 많은 순)
                - 예: `sort=viewCount,DESC` (조회수 많은 순)
                - 예: `sort=createdAt,DESC` (최신순)
            - 여러 정렬 기준을 조합할 수 있습니다.
            - 예: `sort=likeCount,DESC&sort=createdAt,DESC` → 좋아요 순 → 같은 경우 최신순
            기본값: page=0, size=6, sort=createdAt,DESC
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/users/{userId}")
    public SuccessResponse<UserPostsResponseDTO> getPostsByUser(
            @PathVariable UUID userId,
            @RequestParam(required = false) Long categoryId,
            @ParameterObject
            @PageableDefault(size = 6, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        UserPostsResponseDTO responseDTO = blogPostService.getPostsByUser(
                userId,
                categoryId,
                pageable
        );
        return SuccessResponse.of(responseDTO);
    }

    // 특정 사용자가 좋아요를 누른 게시글 조회
    @Operation(summary = "좋아요 누른 게시글 목록 조회", description = "특정 사용자가 좋아요를 누른 게시글 목록을 페이지 단위로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping("/users/{userId}/likes")
    public SuccessResponse<Page<BlogPostSummaryDTO>> getLikedPosts(
            @PathVariable UUID userId,
            @ParameterObject @PageableDefault(size = 6, sort = "post.createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return SuccessResponse.of(blogPostService.getLikedPosts(userId, pageable));
    }

    // 게시글 삭제
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.", security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "403", description = "작성자만 삭제 가능",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{postId}")
    public SuccessResponse<Void> deletePost(@PathVariable Long postId,
                                            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId) {
        blogPostService.deletePost(postId, userId);
        return SuccessResponse.of();
    }

    // 게시글 수정
    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.", security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "403", description = "게시글 혹은 카테고리에 대한 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{postId}")
    public SuccessResponse<Void> updatePost(@PathVariable Long postId,
                                            @Valid @RequestBody BlogPostRequestDTO request,
                                            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId) {
        blogPostService.updatePost(postId, request, userId);
        return SuccessResponse.of();
    }

    // 좋아요 API
    @Operation(summary = "게시글 좋아요 토글", description = "특정 게시글에 대해 좋아요 또는 좋아요 취소를 수행합니다. 이미 좋아요를 눌렀다면 취소됩니다.", security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 처리 성공 (true: 좋아요 추가, false: 좋아요 취소)",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "401", description = "JWT 인증 누락",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시글 또는 사용자 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{postId}/like")
    public SuccessResponse<Boolean> toggleLike(@PathVariable Long postId,
                                               @Parameter(hidden = true) @AuthenticationPrincipal UUID userId) {
        boolean liked = blogPostService.toggleLike(postId, userId);
        return SuccessResponse.of(liked);
    }

    // 조회수 API
    @Operation(summary = "게시글 조회수 증가", description = "특정 게시글의 조회수를 1 증가시킵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회수 증가 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{postId}/view")
    public SuccessResponse<Void> incrementView(@PathVariable Long postId) {
        blogPostService.incrementViewCount(postId);
        return SuccessResponse.of();
    }

}
