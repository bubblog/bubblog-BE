package Bubble.bubblog.domain.comment.service.commentservice;

import Bubble.bubblog.domain.comment.dto.CommentMapper;
import Bubble.bubblog.domain.comment.dto.ReplyCount;
import Bubble.bubblog.domain.comment.dto.req.CreateCommentDTO;
import Bubble.bubblog.domain.comment.dto.res.CommentResponseDTO;
import Bubble.bubblog.domain.comment.dto.res.CommentThreadResponseDTO;
import Bubble.bubblog.domain.comment.entity.Comment;
import Bubble.bubblog.domain.comment.repository.CommentRepository;
import Bubble.bubblog.domain.post.entity.BlogPost;
import Bubble.bubblog.domain.post.repository.BlogPostRepository;
import Bubble.bubblog.domain.user.entity.User;
import Bubble.bubblog.domain.user.repository.UserRepository;
import Bubble.bubblog.global.exception.CustomException;
import Bubble.bubblog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final BlogPostRepository blogPostRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;


    /** 댓글 생성 */
    @Transactional
    @Override
    public CommentResponseDTO createComment(CreateCommentDTO request, Long postId, UUID userId) {
        BlogPost blogPost = blogPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Comment comment;

        if (request.getParentId() != null) {    // 대댓글 생성
            Comment parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.PARENT_COMMENT_NOT_FOUND));

            if (parentComment.getParent() != null) {
                throw new CustomException(ErrorCode.REPLY_TO_REPLY_NOT_ALLOWED);
            }

            if (!parentComment.getPost().getId().equals(postId)) {
                throw new CustomException(ErrorCode.INVALID_PARENT_FOR_POST);
            }

            if (parentComment.getIsDeleted()) {
                throw new CustomException(ErrorCode.REPLY_TO_DELETED_COMMENT_NOT_ALLOWED);
            }

            comment = Comment.createReply(request.getContent(), blogPost, user, parentComment);
            commentRepository.save(comment);

            // 자식 응답: parentId 세팅, replyCount=null
            return commentMapper.toChild(comment);

        } else {
            // 루트 댓글 생성
            comment = Comment.createComment(request.getContent(), blogPost, user);
            commentRepository.save(comment);

            // 루트 응답: parentId=null, replyCount=0L
            return commentMapper.toRoot(comment, 0L);
        }

    }


    /** 특정 게시글의 루트 댓글 목록 조회 */
    @Transactional(readOnly = true)
    @Override
    public List<CommentResponseDTO> getRootCommentsByPost(Long postId) {
        
        // 루트 댓글 리스트 조회
        List<Comment> rootComments = commentRepository.findRootCommentsByPostId(postId);
        if (rootComments.isEmpty()) return List.of();

        // 루트 댓글들의 ID만 뽑아서 집계 쿼리 호출 -> (rootId, cnt) 리스트를 맵으로 변환
        Map<Long, Long> replyCount = commentRepository.countByParentIds(rootComments.stream().map(Comment::getId).toList())
                .stream()
                .collect(Collectors.toMap(
                        ReplyCount::getRootId,
                        ReplyCount::getCnt
                ));

        // Mapper에게 root와 cnt 전달. replyCount 맵에서 rootId 키가 존재하면 해당 cnt를, 존재하지 않으면 0L를 반환
        return rootComments.stream()
                .map(root -> commentMapper.toRoot(root, replyCount.getOrDefault(root.getId(), 0L)))
                .toList();
    }


    /** 특정 루트에 대한 자식 목록 조회(페이징) */
    @Transactional(readOnly = true)
    @Override
    public Page<CommentResponseDTO> getChildrenByRoot(Long commentId, Pageable pageable) {
        // 루트 댓글 존재 여부 확인 - user와 fetch join해서 불러올 필요가 없으므로 findById로 찾는다.
        Comment rootComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (rootComment.getParent() != null) {
            throw new CustomException(ErrorCode.NOT_ROOT_COMMENT);
        }

        // 특정 루트 댓글의 자식들만 페이징하여 조회
        Page<Comment> children = commentRepository.findChildrenByParentId(commentId, pageable);

        return children.map(commentMapper::toChild);
    }


    /** 특정 댓글 단건 조회 */
    @Transactional(readOnly = true)
    @Override
    public CommentResponseDTO getCommentDetail(Long commentId) {
        Comment comment = commentRepository.findByIdWithUser(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        long replyCount = 0L;

        if (comment.getParent() == null) {      // 루트 댓글인 경우에만 집계, 조회하고자하는 댓글이 자식 댓글이라면 replyCount는 null
            replyCount = commentRepository.countByParentId(comment.getId());

            return commentMapper.toRoot(comment, replyCount);
        }

        return commentMapper.toChild(comment);

    }


    /** 댓글 수정 */
    @Transactional
    @Override
    public CommentResponseDTO updateComment(Long commentId, UUID userId, String content) {
        Comment comment = commentRepository.findByIdWithUser(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NO_PERMISSION_TO_EDIT_COMMENT);
        }

        comment.updateContent(content);

        if (comment.getParent() != null) {    // 수정한 댓글이 부모가 존재한다면
            return commentMapper.toChild(comment);

        } else {  // 수정한 댓글이 루트 댓글이라면
            long replyCount = commentRepository.countByParentId(commentId);
            return commentMapper.toRoot(comment, replyCount);
        }
    }


    /** 댓글 삭제 */
    @Transactional
    @Override
    public void deleteComment(Long commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)   // user 닉네임이나 프로필 이미지가 필요없으니 fetch join 하지 않는 findById로!
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NO_PERMISSION_TO_DELETE_COMMENT);
        }

        comment.softDelete(); // isDeleted = true, deletedAt 생성
    }


    /** 특정 루트 댓글을 그의 모든 자식들과 함께 조회 (스레드 조회) */
    @Transactional(readOnly = true)
    @Override
    public CommentThreadResponseDTO getThreadByRoot(Long commentId) {
        Comment rootComment = commentRepository.findByIdWithUser(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (rootComment.getParent() != null) {  // 자식 id가 들어오면 예외 처리
            throw new CustomException(ErrorCode.NOT_ROOT_COMMENT);
        }

        List<Comment> children = commentRepository.findChildrenByRootIdForThread(commentId);
        long replyCount = children.size(); // 이미 로딩됨 → 추가 쿼리 없음

        return commentMapper.toThread(rootComment, replyCount, children);
    }


    /** 게시글 전체 댓글 수 조회 */
    @Transactional(readOnly = true)
    @Override
    public Long getCommentCountForPost(Long postId) {
        // postId에 해당하는 게시글이 존재하는지 확인
        if (!blogPostRepository.existsById(postId)) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }
        return commentRepository.countByPostId(postId);
    }


    /** 내가 쓴 댓글 목록 조회 */
    @Transactional(readOnly = true)
    @Override
    public Page<CommentResponseDTO> getMyComments(UUID userId, Pageable pageable) {
        // userId로 댓글을 찾고, DTO로 변환하여 반환
        Page<Comment> comments = commentRepository.findByUserId(userId, pageable);

        // 각 댓글에 대한 답글 수 집계 (루트 댓글인 경우)
        return comments.map(comment -> {
            Long replyCount = null;
            if (comment.getParent() == null) {
                replyCount = commentRepository.countByParentId(comment.getId());
                return commentMapper.toRoot(comment, replyCount);
            }
            return commentMapper.toChild(comment);
        });
    }
}
