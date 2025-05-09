package Bubble.bubblog.domain.post.service;

import Bubble.bubblog.domain.post.dto.req.BlogPostRequestDTO;
import Bubble.bubblog.domain.post.dto.res.BlogPostDetailDTO;
import Bubble.bubblog.domain.post.dto.res.BlogPostSummaryDTO;

import java.util.List;
import java.util.UUID;

public interface BlogPostService {
    BlogPostDetailDTO createPost(BlogPostRequestDTO request, UUID userId);
    BlogPostDetailDTO getPost(Long postId, UUID userId);
    List<BlogPostSummaryDTO> getAllPosts();
    List<BlogPostSummaryDTO> getPostsByUser(UUID targetUserId, UUID requesterUserId);
    void deletePost(Long postId, UUID userId);
    BlogPostDetailDTO updatePost(Long postId, BlogPostRequestDTO request, UUID userId);
}
