package Bubble.bubblog.global.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AiEmbeddingRequestDTO {
    private Long postId;
    private String content;
}
