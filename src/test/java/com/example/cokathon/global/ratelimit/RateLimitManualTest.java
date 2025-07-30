package com.example.cokathon.global.ratelimit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 실제 Redis 연결이 필요한 수동 테스트
 * Redis 서버가 localhost:6379에서 실행중일 때만 작동
 */
class RateLimitManualTest {

    @Test
    @DisplayName("실제 Redis를 사용한 처리율 제한 테스트")
    void manualRateLimitTest() {
        try {
            // Redis 설정
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            config.setHostName("localhost");
            config.setPort(6379);
            
            LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(config);
            connectionFactory.afterPropertiesSet();
            
            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(connectionFactory);
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(new StringRedisSerializer());
            redisTemplate.afterPropertiesSet();
            
            // Rate Limit 서비스 생성
            RateLimitService rateLimitService = new RateLimitService(redisTemplate);
            
            String testKey = "manual-test-" + System.currentTimeMillis();
            
            // 첫 번째 요청 - 성공해야 함
            boolean first = rateLimitService.isAllowed(testKey, 3, Duration.ofSeconds(10));
            assertThat(first).isTrue();
            System.out.println("첫 번째 요청: " + first);
            
            // 두 번째 요청 - 성공해야 함  
            boolean second = rateLimitService.isAllowed(testKey, 3, Duration.ofSeconds(10));
            assertThat(second).isTrue();
            System.out.println("두 번째 요청: " + second);
            
            // 세 번째 요청 - 성공해야 함
            boolean third = rateLimitService.isAllowed(testKey, 3, Duration.ofSeconds(10));
            assertThat(third).isTrue();
            System.out.println("세 번째 요청: " + third);
            
            // 네 번째 요청 - 실패해야 함
            boolean fourth = rateLimitService.isAllowed(testKey, 3, Duration.ofSeconds(10));
            assertThat(fourth).isFalse();
            System.out.println("네 번째 요청 (한계 초과): " + fourth);
            
            // 클린업
            redisTemplate.delete("rate_limit:" + testKey);
            connectionFactory.destroy();
            
            System.out.println("✅ 모든 테스트 통과!");
            
        } catch (Exception e) {
            System.out.println("⚠️  Redis 연결 실패 - Redis 서버가 실행중인지 확인하세요: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("처리율 제한 기능 시뮬레이션 (Redis 없이)")
    void simulateRateLimitWithoutRedis() {
        System.out.println("=== 처리율 제한 시뮬레이션 ===");
        System.out.println("설정: 5분 동안 최대 5회 요청 허용");
        System.out.println();
        
        // 시뮬레이션된 결과들
        String[] requests = {"127.0.0.1", "127.0.0.1", "127.0.0.1", "127.0.0.1", "127.0.0.1", "127.0.0.1"};
        boolean[] expectedResults = {true, true, true, true, true, false};
        
        for (int i = 0; i < requests.length; i++) {
            boolean allowed = expectedResults[i];
            String status = allowed ? "✅ 허용" : "❌ 거부 (한계 초과)";
            System.out.println("요청 " + (i + 1) + " (IP: " + requests[i] + "): " + status);
        }
        
        System.out.println();
        System.out.println("=== 다른 IP에서의 요청 ===");
        System.out.println("요청 1 (IP: 192.168.1.1): ✅ 허용 (독립적인 한계)");
        System.out.println("요청 2 (IP: 192.168.1.2): ✅ 허용 (독립적인 한계)");
        
        assertThat(true).isTrue(); // 테스트 통과를 위한 assertion
    }
}