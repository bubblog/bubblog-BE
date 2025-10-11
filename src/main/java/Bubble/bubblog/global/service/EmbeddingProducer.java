package Bubble.bubblog.global.service;

import Bubble.bubblog.global.exception.CustomException;
import Bubble.bubblog.global.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingProducer {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String QUEUE_KEY = "embedding:queue";

    public void sendEmbeddingRequest(Long postId, Boolean title, Boolean content) {
        try {
            Map<String, Object> message = Map.of(
                    "postId", postId,
                    "title", title,
                    "content", content
            );

            String json = objectMapper.writeValueAsString(message);
            redisTemplate.opsForList().leftPush(QUEUE_KEY, json);

            log.info("✅ [Redis] Sent embedding request for postId: {}", postId);
        } catch (JsonProcessingException e) {
            log.error("❌ Failed to serialize embedding request for post {}", postId, e);
            throw new CustomException(ErrorCode.SERIALIZATION_ERROR, e);
        }
    }
}
