package com.example.cokathon.global.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class RateLimitServiceIntegrationTest {

    @Autowired
    private RateLimitService rateLimitService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @DisplayName("처음 요청은 허용된다")
    void firstRequest_ShouldBeAllowed() {
        String key = "test-key-1";
        boolean result = rateLimitService.isAllowed(key, 5, Duration.ofMinutes(1));
        
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("제한 내의 연속 요청들은 모두 허용된다")
    void consecutiveRequestsWithinLimit_ShouldAllBeAllowed() {
        String key = "test-key-2";
        
        for (int i = 0; i < 5; i++) {
            boolean result = rateLimitService.isAllowed(key, 5, Duration.ofMinutes(1));
            assertThat(result).isTrue();
        }
    }

    @Test
    @DisplayName("제한을 초과한 요청은 거부된다")
    void requestExceedingLimit_ShouldBeRejected() {
        String key = "test-key-3";
        
        for (int i = 0; i < 5; i++) {
            rateLimitService.isAllowed(key, 5, Duration.ofMinutes(1));
        }
        
        boolean result = rateLimitService.isAllowed(key, 5, Duration.ofMinutes(1));
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("서로 다른 키는 독립적으로 처리율 제한이 적용된다")
    void differentKeys_ShouldHaveIndependentRateLimits() {
        String key1 = "test-key-4";
        String key2 = "test-key-5";
        
        for (int i = 0; i < 5; i++) {
            rateLimitService.isAllowed(key1, 5, Duration.ofMinutes(1));
        }
        
        boolean key1Result = rateLimitService.isAllowed(key1, 5, Duration.ofMinutes(1));
        boolean key2Result = rateLimitService.isAllowed(key2, 5, Duration.ofMinutes(1));
        
        assertThat(key1Result).isFalse();
        assertThat(key2Result).isTrue();
    }

    @Test
    @DisplayName("시간 윈도우가 지나면 다시 허용된다")
    void afterTimeWindow_ShouldBeAllowedAgain() throws InterruptedException {
        String key = "test-key-6";
        Duration shortWindow = Duration.ofSeconds(1);
        
        for (int i = 0; i < 3; i++) {
            rateLimitService.isAllowed(key, 3, shortWindow);
        }
        
        boolean beforeWait = rateLimitService.isAllowed(key, 3, shortWindow);
        assertThat(beforeWait).isFalse();
        
        TimeUnit.SECONDS.sleep(2);
        
        boolean afterWait = rateLimitService.isAllowed(key, 3, shortWindow);
        assertThat(afterWait).isTrue();
    }

    @Test
    @DisplayName("Redis에 올바른 키 형식으로 저장된다")
    void correctKeyFormat_ShouldBeStoredInRedis() {
        String key = "test-ip-127.0.0.1";
        rateLimitService.isAllowed(key, 5, Duration.ofMinutes(1));
        
        String redisKey = "rate_limit:" + key;
        String storedValue = (String) redisTemplate.opsForValue().get(redisKey);
        
        assertThat(storedValue).isEqualTo("1");
    }
}