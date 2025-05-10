package Bubble.bubblog.domain.post.dto.res;

import Bubble.bubblog.domain.post.entity.BlogPost;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor    // 모든 필드 값을 파라미터로 받는 생성자를 생성
public class BlogPostDetailDTO {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private boolean publicVisible;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private Long categoryId;
    private AuthorDTO author;

    public BlogPostDetailDTO(BlogPost post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.summary = post.getSummary();
        this.publicVisible = post.isPublicVisible();
        this.thumbnailUrl = post.getThumbnailUrl();
        this.categoryId = post.getCategory().getId();
        this.createdAt = post.getCreatedAt();

        // 작성자
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

