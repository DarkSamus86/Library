package org.darksamus86.library.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisHealthCheck {

    private final RedisTemplate<String, Object> redisTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void checkRedisConnection() {
        try {
            redisTemplate.opsForValue().set("health:check", "OK", 10, TimeUnit.SECONDS);
            String value = (String) redisTemplate.opsForValue().get("health:check");

            if ("OK".equals(value)) {
                log.info("Redis connection successful");
            } else {
                log.error("Redis health check failed");
            }
        } catch (Exception e) {
            log.error("Redis connection failed: {}", e.getMessage());
            throw new IllegalStateException("Cannot connect to Redis", e);
        }
    }
}