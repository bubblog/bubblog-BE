package Bubble.bubblog.domain.post.dto.res;

import Bubble.bubblog.domain.post.entity.BlogPost;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class BlogPostSummaryDTO {
    private Long id;
    private String title;
    private String summary;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private Long viewCount;
    private Long likeCount;
    private UUID userId;

    public BlogPostSummaryDTO(BlogPost post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.summary = post.getSummary();
        this.thumbnailUrl = post.getThumbnailUrl();
        this.createdAt = post.getCreatedAt();
        this.viewCount = post.getViewCount();
        this.likeCount = post.getLikeCount();
        this.userId = post.getUser().getId();
    }
}
