package Bubble.bubblog.domain.comment.controller;

import Bubble.bubblog.domain.comment.service.commentlikeservice.CommentLikeService;
import Bubble.bubblog.global.dto.SuccessResponse;
import Bubble.bubblog.global.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io. swagger. v3.oas. annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/comments/{commentId}/likes", produces = "application/json")
@Tag(name = "CommentLike", description = "댓글 좋아요 관련 API")
public class CommentLikeController {
    private final CommentLikeService commentLikeService;
    
    
    /** 댓글 좋아요/취소 */
    @Operation(summary = "댓글 좋아요/좋아요 취소", description = "댓글 좋아요를 토글합니다.", security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 좋아요 성공", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public SuccessResponse<Void> toggleCommentLike(@PathVariable Long commentId, @Parameter(hidden = true) @AuthenticationPrincipal UUID userId) {
        commentLikeService.toggleCommentLike(commentId, userId);
        return SuccessResponse.of();
    }
}
