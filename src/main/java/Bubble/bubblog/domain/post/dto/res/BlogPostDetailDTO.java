package Bubble.bubblog.domain.post.dto.res;

import Bubble.bubblog.domain.post.entity.BlogPost;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class BlogPostDetailDTO {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private boolean publicVisible;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private Long categoryId;
    private UUID userId; // ✅ author 대신 userId만

    public BlogPostDetailDTO(BlogPost post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.summary = post.getSummary();
        this.publicVisible = post.isPublicVisible();
        this.thumbnailUrl = post.getThumbnailUrl();
        this.categoryId = post.getCategory().getId();
        this.createdAt = post.getCreatedAt();
        this.userId = post.getUser().getId(); // ✅ 작성자 UUID만 전달
    }
}
