package Bubble.bubblog.domain.post.dto.res;

import Bubble.bubblog.domain.post.entity.BlogPost;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BlogPostSummaryDTO {
    private Long id;
    private String title;
    private String summary;
    private String thumbnailUrl;
    // private LocalDateTime createdAt;
    private AuthorDTO author;

    public BlogPostSummaryDTO(BlogPost post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.summary = post.getSummary();
        this.thumbnailUrl = post.getThumbnailUrl();
        // this.createdAt = post.getCreatedAt();
        this.author = new AuthorDTO(
                post.getUser().getNickname()
                // post.getUser().getProfileImageUrl() // 향후 추가
        );
    }

    @Getter
    @AllArgsConstructor
    public static class AuthorDTO {
        private String nickname;
        // private String profileImageUrl;
    }
}
