package Bubble.bubblog.domain.post.dto.res;

import Bubble.bubblog.domain.post.entity.BlogPost;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
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
    private UUID userId;
    private String nickname;
    private List<String> categoryList;

    public BlogPostDetailDTO(BlogPost post, List<String> categoryList) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.summary = post.getSummary();
        this.publicVisible = post.isPublicVisible();
        this.thumbnailUrl = post.getThumbnailUrl();
        this.categoryId = post.getCategory().getId();
        this.createdAt = post.getCreatedAt();
        this.userId = post.getUser().getId();
        this.nickname = post.getUser().getNickname();
        this.categoryList = categoryList;
    }
}
