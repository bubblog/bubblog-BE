package Bubble.bubblog.domain.comment.service.commentservice;

import Bubble.bubblog.domain.comment.dto.req.CreateCommentDTO;
import Bubble.bubblog.domain.comment.dto.res.CommentResponseDTO;
import Bubble.bubblog.domain.comment.dto.res.CommentThreadResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    CommentResponseDTO createComment(CreateCommentDTO request, Long postId, UUID userId);
    List<CommentResponseDTO> getRootCommentsByPost(Long postId);
    Page<CommentResponseDTO> getChildrenByRoot(Long rootCommentId, Pageable pageable);
    CommentResponseDTO getCommentDetail(Long commentId);
    CommentThreadResponseDTO getThreadByRoot(Long commentId);
    CommentResponseDTO updateComment(Long commentId, UUID userId, String content);
    void deleteComment(Long commentId, UUID userId);
    Long getCommentCountForPost(Long postId);

    Page<CommentResponseDTO> getMyComments(UUID userId, Pageable pageable);
}
