package Bubble.bubblog.global.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@AllArgsConstructor
public class ContentEmbeddingRequestDTO {
    @JsonProperty("post_id")
    private Long postId;

    private String content;
}
