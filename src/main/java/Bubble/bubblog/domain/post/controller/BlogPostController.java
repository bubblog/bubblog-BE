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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Tag(name = "Blog Post", description = "블로그 게시글 관련 API")
@RestController
@RequestMapping(value = "/api/blogs", produces = "application/json")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class BlogPostController {

    private final BlogPostService blogPostService;

    @Operation(summary = "블로그 포스트 생성", description = "사용자가 새 게시글을 작성합니다.")
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
        // 기존에는 게시글 생성 후 ok응답만 했다면 이번 리팩토링 후 게시글 생성 후 게시글 상세 내용 반환,,,
        BlogPostDetailDTO dto = blogPostService.createPost(request, userId);
        return SuccessResponse.of(dto);
    }

    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "403", description = "비공개 게시글 접근",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{postId}")
    public SuccessResponse<BlogPostDetailDTO> getPost(@PathVariable Long postId, @Parameter(hidden = true) @AuthenticationPrincipal UUID userId) {  // @PathVariable은 URL 경로에 포함된 값을 컨트롤러 메서드의 파라미터로 바인딩해주는 역할을 함
        return SuccessResponse.of(blogPostService.getPost(postId, userId));
    }

    @Operation(summary = "공개 게시글 전체 조회", description = "전체 공개된 게시글을 리스트 형태로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    })
    @GetMapping
    public SuccessResponse<List<BlogPostSummaryDTO>> getAllPosts() {
        return SuccessResponse.of(blogPostService.getAllPosts());
    }

    @Operation(summary = "사용자 게시글 조회", description = "특정 사용자의 게시글을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "403", description = "비공개 게시글 접근",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/users/{userId}")
    public SuccessResponse<UserPostsResponseDTO> getPostsByUser(@PathVariable UUID userId,
                                                                    @Parameter(hidden = true) @AuthenticationPrincipal UUID requesterId) {
        List<BlogPostSummaryDTO> posts = blogPostService.getPostsByUser(userId, requesterId);
        return SuccessResponse.of(new UserPostsResponseDTO(userId, posts));
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
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

    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
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


}
