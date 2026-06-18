package org.darksamus86.library.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenStorageServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private TokenStorageService tokenStorageService;

    @Test
    @DisplayName("Should save refresh token")
    void saveToken_ShouldStoreInRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        tokenStorageService = new TokenStorageService(redisTemplate);

        tokenStorageService.saveToken("testuser", "refreshToken123");

        verify(valueOperations).set("refresh_token:testuser", "refreshToken123", Duration.ofDays(7));
    }

    @Test
    @DisplayName("Should return true when token is valid")
    void isValidToken_WhenTokenMatches_ShouldReturnTrue() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:testuser")).thenReturn("refreshToken123");
        tokenStorageService = new TokenStorageService(redisTemplate);

        boolean result = tokenStorageService.isValidToken("testuser", "refreshToken123");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when token does not match")
    void isValidToken_WhenTokenDoesNotMatch_ShouldReturnFalse() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:testuser")).thenReturn("differentToken");
        tokenStorageService = new TokenStorageService(redisTemplate);

        boolean result = tokenStorageService.isValidToken("testuser", "refreshToken123");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when no token stored")
    void isValidToken_WhenNoTokenStored_ShouldReturnFalse() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:testuser")).thenReturn(null);
        tokenStorageService = new TokenStorageService(redisTemplate);

        boolean result = tokenStorageService.isValidToken("testuser", "refreshToken123");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should revoke token")
    void revokeToken_ShouldDeleteFromRedis() {
        when(redisTemplate.delete("refresh_token:testuser")).thenReturn(true);
        tokenStorageService = new TokenStorageService(redisTemplate);

        tokenStorageService.revokeToken("testuser");

        verify(redisTemplate).delete("refresh_token:testuser");
    }
}
