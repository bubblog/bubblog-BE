package Bubble.bubblog.domain.post.dto.res;

import java.util.List;
import java.util.UUID;

public record UserPostsResponseDTO(
        UUID userId,
        String nickname,
        List<BlogPostSummaryDTO> posts,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {}

