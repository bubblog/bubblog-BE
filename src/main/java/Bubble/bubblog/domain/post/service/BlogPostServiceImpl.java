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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

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
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 조회하고자 하는 게시글이 비공개라면 작성자만이 이를 조회할 수 있음
        boolean isOwner = post.getUser().getId().equals(userId);
        if (!post.isPublicVisible() && !isOwner) {
            throw new IllegalArgumentException("비공개 게시글입니다.");
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
    public List<BlogPostSummaryDTO> getPostsByUser(UUID targetUserId, UUID requesterUserId) {
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
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
        }

        blogPostRepository.delete(post);
    }

    @Transactional
    @Override
    public BlogPostDetailDTO updatePost(Long postId, BlogPostRequestDTO request, UUID userId) {
        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

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
