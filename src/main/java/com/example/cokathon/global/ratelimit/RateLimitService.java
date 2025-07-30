package com.example.cokathon.global.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean isAllowed(String key, int maxRequests, Duration timeWindow) {
        String redisKey = "rate_limit:" + key;
        
        String currentCount = (String) redisTemplate.opsForValue().get(redisKey);
        
        if (currentCount == null) {
            redisTemplate.opsForValue().set(redisKey, "1", timeWindow);
            return true;
        }
        
        int count = Integer.parseInt(currentCount);
        if (count >= maxRequests) {
            return false;
        }
        
        redisTemplate.opsForValue().increment(redisKey);
        return true;
    }
}