package Bubble.bubblog.domain.post.service;

import Bubble.bubblog.domain.category.entity.Category;
import Bubble.bubblog.domain.category.repository.CategoryRepository;
import Bubble.bubblog.domain.post.dto.req.BlogPostRequestDTO;
import Bubble.bubblog.domain.post.dto.res.BlogPostDetailDTO;
import Bubble.bubblog.domain.post.dto.res.BlogPostSummaryDTO;
import Bubble.bubblog.domain.post.entity.BlogPost;
import Bubble.bubblog.domain.post.repository.BlogPostRepository;
import Bubble.bubblog.domain.user.entity.User;
import Bubble.bubblog.domain.user.repository.UserRepository;
import Bubble.bubblog.global.exception.CustomException;
import Bubble.bubblog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlogPostServiceImpl implements BlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public BlogPostDetailDTO createPost(BlogPostRequestDTO request, UUID userId) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new CustomException(ErrorCode.INVALID_POST_INPUT);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        BlogPost blogPost = BlogPost.of(
                request.getTitle(),
                request.getContent(),
                request.getSummary(),
                request.isPublicVisible(),
                request.getThumbnailUrl(),
                user,
                category
        );

        BlogPost post = blogPostRepository.save(blogPost);
        return new BlogPostDetailDTO(post);
    }

    @Transactional(readOnly = true)
    @Override
    public BlogPostDetailDTO getPost(Long postId, UUID userId) {
        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 조회하고자 하는 게시글이 비공개라면 작성자만이 이를 조회할 수 있음
        boolean isOwner = post.getUser().getId().equals(userId);
        if (!post.isPublicVisible() && !isOwner) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_POST_ACCESS);
        }

        return new BlogPostDetailDTO(post);
    }

    // 모든 게시글 보기
    @Transactional(readOnly = true)
    @Override
    public List<BlogPostSummaryDTO> getAllPosts() {
        return blogPostRepository.findAllByPublicVisibleTrue().stream()
                .map(BlogPostSummaryDTO::new)
                .toList();
    }

    // 특정 사용자의 게시글 목록 조회
    @Transactional(readOnly = true)
    @Override
    public List<BlogPostSummaryDTO> getPostsByUser(UUID targetUserId, UUID requesterUserId) {
        userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        boolean isOwner = targetUserId.equals(requesterUserId);
        List<BlogPost> posts;

        if (isOwner) {
            posts = blogPostRepository.findAllByUserId(targetUserId);
        } else {
            posts = blogPostRepository.findAllByUserIdAndPublicVisibleTrue(targetUserId);
        }

        return posts.stream()
                .map(BlogPostSummaryDTO::new)
                .toList();
    }

    @Transactional
    @Override
    public void deletePost(Long postId, UUID userId) {
        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_POST_ACCESS);
        }

        blogPostRepository.delete(post);
    }

    @Transactional
    @Override
    public BlogPostDetailDTO updatePost(Long postId, BlogPostRequestDTO request, UUID userId) {
        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_POST_ACCESS);
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        post.update(
                request.getTitle(),
                request.getContent(),
                request.getSummary(),
                request.isPublicVisible(),
                request.getThumbnailUrl(),
                category
        );

        return new BlogPostDetailDTO(post);
    }

}
