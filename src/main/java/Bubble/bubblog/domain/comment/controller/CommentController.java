package Bubble.bubblog.domain.comment.controller;

import Bubble.bubblog.domain.comment.dto.req.UpdateCommentDTO;
import Bubble.bubblog.domain.comment.dto.res.CommentResponseDTO;
import Bubble.bubblog.domain.comment.dto.res.CommentThreadResponseDTO;
import Bubble.bubblog.domain.comment.service.commentservice.CommentService;
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
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor   // DI를 위함
@RequestMapping(value = "/api/comments", produces = "application/json")
@Tag(name = "Comment", description = "게시글 댓글 관련 API")   // 스웨거 전용
public class CommentController {
    private final CommentService commentService;


    /** 특정 댓글 단건 조회 */
    @Operation(summary = "댓글 단건 상세 조회", description = "특정 commentId의 단건 상세를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{commentId}")
    public SuccessResponse<CommentResponseDTO> getCommentDetail(@PathVariable Long commentId) {
        CommentResponseDTO dto = commentService.getCommentDetail(commentId);
        return SuccessResponse.of(dto);
    }

    /** 특정 루트 댓글의 자식 댓글 목록을 페이징으로 조회 */
    @Operation(summary = "루트 댓글의 자식들 페이징 조회", description = "특정 루트 댓글의 자식들을 페이징 처리하여 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 루트 댓글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{commentId}/children")
    public SuccessResponse<Page<CommentResponseDTO>> getChildrenByRoot(@PathVariable Long commentId, @ParameterObject @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<CommentResponseDTO> dto = commentService.getChildrenByRoot(commentId, pageable);
        return SuccessResponse.of(dto);
    }


    /** 특정 루트 댓글과 그 모든 자식 댓글 함께 조회 (스레드 조회) */
    @Operation(summary = "루트 댓글과 모든 자식들을 함께 스레드로 조회", description = "루트 댓글 하나와 그 자식(대댓글)들을 함께 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 루트 댓글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{commentId}/thread")
    public SuccessResponse<CommentThreadResponseDTO> getThreadByRoot(@PathVariable Long commentId) {
        CommentThreadResponseDTO dto = commentService.getThreadByRoot(commentId);
        return SuccessResponse.of(dto);
    }


    /** 댓글 수정 */
    @Operation(summary = "댓글 수정", description = "본인이 작성한 댓글만 수정할 수 있습니다.", security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "입력값이 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{commentId}")
    public SuccessResponse<CommentResponseDTO> updateComment(@PathVariable Long commentId, @Valid @RequestBody UpdateCommentDTO request,
                                                            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId
    ) {
        String newContent = request.getContent();
        CommentResponseDTO dto = commentService.updateComment(commentId, userId, newContent);
        return SuccessResponse.of(dto);
    }


    /** 댓글 삭제 */
    @Operation(summary = "댓글 삭제", description = "본인이 작성한 댓글만 삭제(Soft delete)할 수 있습니다.", security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{commentId}")
    public SuccessResponse<Void> deleteComment(@PathVariable Long commentId, @Parameter(hidden = true) @AuthenticationPrincipal UUID userId) {
        commentService.deleteComment(commentId, userId);
        return SuccessResponse.of();
    }

}