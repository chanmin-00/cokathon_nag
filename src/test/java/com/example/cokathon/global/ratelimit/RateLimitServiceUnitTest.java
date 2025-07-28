package com.example.cokathon.global.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
class RateLimitServiceUnitTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("처음 요청시 허용되고 Redis에 카운트 1로 저장")
    void firstRequest_ShouldBeAllowed() {
        String key = "test-key";
        String redisKey = "rate_limit:" + key;
        
        when(valueOperations.get(redisKey)).thenReturn(null);

        boolean result = rateLimitService.isAllowed(key, 5, Duration.ofMinutes(1));

        assertThat(result).isTrue();
        verify(valueOperations).set(eq(redisKey), eq("1"), eq(Duration.ofMinutes(1)));
    }

    @Test
    @DisplayName("제한 내의 요청시 허용되고 카운트가 증가")
    void requestWithinLimit_ShouldBeAllowed() {
        String key = "test-key";
        String redisKey = "rate_limit:" + key;
        
        when(valueOperations.get(redisKey)).thenReturn("3");

        boolean result = rateLimitService.isAllowed(key, 5, Duration.ofMinutes(1));

        assertThat(result).isTrue();
        verify(valueOperations).increment(redisKey);
    }

    @Test
    @DisplayName("제한을 초과한 요청시 거부")
    void requestExceedingLimit_ShouldBeRejected() {
        String key = "test-key";
        String redisKey = "rate_limit:" + key;
        
        when(valueOperations.get(redisKey)).thenReturn("5");

        boolean result = rateLimitService.isAllowed(key, 5, Duration.ofMinutes(1));

        assertThat(result).isFalse();
        verify(valueOperations, never()).increment(any());
        verify(valueOperations, never()).set(any(), any(), any(Duration.class));
    }

    @Test
    @DisplayName("정확히 한계에 도달한 요청시 거부")
    void requestAtExactLimit_ShouldBeRejected() {
        String key = "test-key";
        String redisKey = "rate_limit:" + key;
        
        when(valueOperations.get(redisKey)).thenReturn("10");

        boolean result = rateLimitService.isAllowed(key, 10, Duration.ofMinutes(1));

        assertThat(result).isFalse();
        verify(valueOperations, never()).increment(any());
    }

    @Test
    @DisplayName("올바른 Redis 키 형식을 사용")
    void correctRedisKeyFormat_ShouldBeUsed() {
        String key = "192.168.1.1";
        String expectedRedisKey = "rate_limit:192.168.1.1";
        
        when(valueOperations.get(expectedRedisKey)).thenReturn(null);

        rateLimitService.isAllowed(key, 5, Duration.ofMinutes(1));

        verify(valueOperations).get(expectedRedisKey);
        verify(valueOperations).set(eq(expectedRedisKey), eq("1"), any(Duration.class));
    }
}