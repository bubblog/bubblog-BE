package Bubble.bubblog.domain.user.controller;

import Bubble.bubblog.domain.comment.dto.res.CommentResponseDTO;
import Bubble.bubblog.domain.comment.service.commentservice.CommentService;
import Bubble.bubblog.domain.post.dto.res.BlogPostDetailDTO;
import Bubble.bubblog.domain.post.dto.res.BlogPostSummaryDTO;
import Bubble.bubblog.domain.post.dto.res.UserPostsResponseDTO;
import Bubble.bubblog.domain.post.service.BlogPostService;
import Bubble.bubblog.domain.user.dto.infoRes.UserInfoDTO;
import Bubble.bubblog.domain.user.dto.req.UserUpdateDTO;
import Bubble.bubblog.domain.user.service.UserInfoService;
import Bubble.bubblog.global.dto.ErrorResponse;
import Bubble.bubblog.global.dto.SuccessResponse;
import Bubble.bubblog.global.dto.swaggerResponse.comment.CommentPageSuccessResponse;
import Bubble.bubblog.global.dto.swaggerResponse.info.UserInfoSuccessResponse;
import Bubble.bubblog.global.dto.swaggerResponse.info.UserPostsSuccessResponse;
import Bubble.bubblog.global.dto.swaggerResponse.post.BlogPostDetailSuccessResponse;
import Bubble.bubblog.global.dto.swaggerResponse.post.BlogPostSummarySuccessResponse;
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

@Tag(name = "User Info", description = "특정 사용자 정보 API")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users", produces = "application/json")
public class UserInfoController {

    private final UserInfoService userInfoService;
    private final BlogPostService blogPostService;
    private final CommentService commentService;

    @Operation(summary = "사용자 정보 조회", description = "특정 사용자의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = UserInfoSuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{userId}")
    public SuccessResponse<UserInfoDTO> getUserInfo(@PathVariable UUID userId) {
        return SuccessResponse.of(userInfoService.getUserInfo(userId));
    }

    @Operation(summary = "사용자 정보 수정", description = "현재 로그인된 유저의 정보를 수정합니다.", security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "입력값 오류 혹은 닉네임 중복", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/me")
    public SuccessResponse<Void> updateUser(@Valid @RequestBody UserUpdateDTO request,
                                            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId) {
        userInfoService.updateUser(userId, request);
        return SuccessResponse.of();
    }

    @Operation(summary = "나의 게시글 상세 조회", description = "로그인한 사용자의 특정 게시글 상세 정보를 조회합니다.", security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = BlogPostDetailSuccessResponse.class))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/me/posts/{postId}")
    public SuccessResponse<BlogPostDetailDTO> getMyPost(
            @PathVariable Long postId,
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId) {     // userId를 @AuthenticationPrincipal로 받음
        return SuccessResponse.of(userInfoService.getMyPost(postId, userId));
    }

    @Operation(
            summary = "나의 게시글 목록 조회",
            description = """
            로그인한 사용자의 모든 게시글 (공개 및 비공개) 목록을 조회합니다.
            - `categoryId` : 선택적으로 하위 카테고리를 포함한 특정 카테고리의 게시글만 조회할 수 있습니다.
            - `sort` : 정렬 기준을 지정할 수 있습니다. (기존과 동일)
            기본값: page=0, size=6, sort=createdAt,DESC
            """,
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = UserPostsSuccessResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/me/posts")
    public SuccessResponse<UserPostsResponseDTO> getMyAllPosts(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
            @RequestParam(required = false) Long categoryId,
            @ParameterObject @PageableDefault(size = 6, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        UserPostsResponseDTO responseDTO = userInfoService.getMyAllPosts(userId, categoryId, pageable);
        return SuccessResponse.of(responseDTO);
    }

    @Operation(summary = "내가 작성한 게시글 중 다른 사람이 댓글을 단 게시글 목록 조회", description = "현재 인증된 사용자가 작성한 게시글 중 하나라도 댓글이 달린 게시글을 조회합니다.", security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = BlogPostSummarySuccessResponse.class)))
    })
    @GetMapping("/me/posts/comments")
    public SuccessResponse<Page<BlogPostSummaryDTO>> getMyPostsWithComments(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
            @ParameterObject @PageableDefault(size = 6, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return SuccessResponse.of(blogPostService.getMyPostsWithComments(userId, pageable));
    }

    @Operation(summary = "내가 좋아요 누른 게시글 목록 조회", description = "내가 좋아요를 누른 게시글 목록을 페이지 단위로 조회합니다.", security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요한 게시글 목록 조회 성공", content = @Content(schema = @Schema(implementation = BlogPostSummarySuccessResponse.class)))
    })
    @GetMapping("/me/likes/posts")
    public SuccessResponse<Page<BlogPostSummaryDTO>> getLikedPosts(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
            @ParameterObject @PageableDefault(size = 6, sort = "post.createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return SuccessResponse.of(blogPostService.getLikedPosts(userId, pageable));
    }

    @Operation(summary = "내가 댓글 단 게시글 목록 조회", description = "내가 댓글 단 게시글 목록을 페이지 단위로 조회합니다.", security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 단 게시글 목록 조회 성공", content = @Content(schema = @Schema(implementation = BlogPostSummarySuccessResponse.class)))
    })
    @GetMapping("/me/comments/posts")
    public SuccessResponse<Page<BlogPostSummaryDTO>> getMyCommentedPosts(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
            @ParameterObject @PageableDefault(size = 6, sort = "post.createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return SuccessResponse.of(blogPostService.getMyCommentedPosts(userId, pageable));
    }

    @Operation(summary = "내가 쓴 댓글 목록 조회", description = "현재 인증된 사용자가 작성한 댓글 목록을 페이지 단위로 최근에 작성한 순으로 조회합니다.", security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = CommentPageSuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/me/comments")
    public SuccessResponse<Page<CommentResponseDTO>> getMyComments(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return SuccessResponse.of(commentService.getMyComments(userId, pageable));
    }
}
