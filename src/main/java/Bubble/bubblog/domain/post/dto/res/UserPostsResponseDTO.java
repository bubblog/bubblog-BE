package Bubble.bubblog.domain.post.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserPostsResponseDTO {
    private UUID userId;
    private List<BlogPostSummaryDTO> posts;
}
