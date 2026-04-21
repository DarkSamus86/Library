package org.darksamus86.library.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenStorageService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String KEY_PREFIX = "refresh_token:";
    private static final Duration TTL = Duration.ofDays(7); // Срок жизни токена

    /**
     * Сохранить refresh-токен в Redis
     */
    public void saveToken(String username, String refreshToken) {
        String key = KEY_PREFIX + username;
        redisTemplate.opsForValue().set(key, refreshToken, TTL);
        log.debug("Refresh token saved for user: {}", username);
    }

    /**
     * Проверить валидность токена
     */
    public boolean isValidToken(String username, String refreshToken) {
        String key = KEY_PREFIX + username;
        Object stored = redisTemplate.opsForValue().get(key);
        return refreshToken.equals(stored);
    }

    /**
     * Отозвать токен (Logout)
     */
    public void revokeToken(String username) {
        String key = KEY_PREFIX + username;
        redisTemplate.delete(key);
        log.info("Refresh token revoked for user: {}", username);
    }
}