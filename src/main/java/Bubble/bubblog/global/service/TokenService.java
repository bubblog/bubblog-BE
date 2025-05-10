package Bubble.bubblog.global.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpireInSeconds;

    public void saveRefreshToken(UUID userId, String refreshToken) {
        redisTemplate.opsForValue().set(
                "RT:" + userId,
                refreshToken,
                refreshTokenExpireInSeconds,
                TimeUnit.SECONDS
        );
    }

    public String getRefreshToken(UUID userId) {
        return redisTemplate.opsForValue().get("RT:" + userId);
    }

    public void deleteRefreshToken(UUID userId) {
        String key = "RT:" + userId;
        Boolean result = redisTemplate.delete(key);
    }
}
