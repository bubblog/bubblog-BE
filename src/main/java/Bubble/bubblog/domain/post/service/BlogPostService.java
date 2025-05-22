package Bubble.bubblog.domain.post.service;

import Bubble.bubblog.domain.post.dto.req.BlogPostRequestDTO;
import Bubble.bubblog.domain.post.dto.res.BlogPostDetailDTO;
import Bubble.bubblog.domain.post.dto.res.BlogPostSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface BlogPostService {
    BlogPostDetailDTO createPost(BlogPostRequestDTO request, UUID userId);
    BlogPostDetailDTO getPost(Long postId, UUID userId);
    Page<BlogPostSummaryDTO> getAllPosts(Pageable pageable);
    List<BlogPostSummaryDTO> getPostsByUser(UUID targetUserId, UUID requesterUserId);
    void deletePost(Long postId, UUID userId);
    BlogPostDetailDTO updatePost(Long postId, BlogPostRequestDTO request, UUID userId);
}
