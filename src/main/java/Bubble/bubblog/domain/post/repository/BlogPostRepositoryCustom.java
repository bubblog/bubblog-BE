package Bubble.bubblog.domain.post.repository;

import Bubble.bubblog.domain.post.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface BlogPostRepositoryCustom {
    Page<BlogPost> searchPosts(String keyword, Pageable pageable);
    Page<BlogPost> searchUserPosts(UUID userId, boolean isOwner, List<Long> categoryIds, Pageable pageable);
}
