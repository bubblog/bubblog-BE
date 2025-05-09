package Bubble.bubblog.domain.post.controller;

import Bubble.bubblog.domain.post.dto.req.BlogPostRequestDTO;
import Bubble.bubblog.domain.post.dto.res.BlogPostDetailDTO;
import Bubble.bubblog.domain.post.dto.res.BlogPostSummaryDTO;
import Bubble.bubblog.domain.post.service.BlogPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Tag(name = "Blog Post", description = "블로그 게시글 관련 API")
@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class BlogPostController {

    private final BlogPostService blogPostService;

    @Operation(summary = "블로그 포스트 생성")
    @PostMapping
    public ResponseEntity<BlogPostDetailDTO> createPost(@RequestBody BlogPostRequestDTO request,
                                           @AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(blogPostService.createPost(request, userId));
    }

    @Operation(summary = "특정 블로그 포스트 조회")
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId, @AuthenticationPrincipal UUID userId) {  // @PathVariable은 URL 경로에 포함된 값을 컨트롤러 메서드의 파라미터로 바인딩해주는 역할을 함
        try {
            return ResponseEntity.ok(blogPostService.getPost(postId, userId));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("로그인 필요");
        }
    }

    @Operation(summary = "모든 블로그 포스트 조회")
    @GetMapping
    public ResponseEntity<List<BlogPostSummaryDTO>> getAllPosts() {
        return ResponseEntity.ok(blogPostService.getAllPosts());
    }

    @Operation(summary = "특정 사용자의 게시글 목록 조회")
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getPostsByUser(@PathVariable UUID userId,
                                                                    @AuthenticationPrincipal UUID requesterId) {
        try {
            return ResponseEntity.ok(blogPostService.getPostsByUser(userId, requesterId));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("로그인 필요");
        }
    }

    @Operation(summary = "블로그 포스트 삭제")
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId,
                                           @AuthenticationPrincipal UUID userId) {
        blogPostService.deletePost(postId, userId);
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }

    @PutMapping("/{postId}")
    public ResponseEntity<BlogPostDetailDTO> updatePost(@PathVariable Long postId,
                                                        @RequestBody BlogPostRequestDTO request,
                                                        @AuthenticationPrincipal UUID userId) {
        BlogPostDetailDTO updated = blogPostService.updatePost(postId, request, userId);
        return ResponseEntity.ok(updated);
    }


}
