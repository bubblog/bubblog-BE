package Bubble.bubblog.global.dto.kafka;

public record EmbeddingRequestKafkaDTO(
        Long postId,
        String text,   // 내용
        EmbeddingType type   // TITLE, CONTENT
) {}
