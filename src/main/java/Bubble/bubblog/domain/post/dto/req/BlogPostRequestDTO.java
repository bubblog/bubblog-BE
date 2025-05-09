package Bubble.bubblog.domain.post.dto.req;

import lombok.Getter;

@Getter
public class BlogPostRequestDTO {
    private String title;
    private String content;
    private String summary;
    private Long categoryId;
    private boolean publicVisible;
    private String thumbnailUrl;
}
