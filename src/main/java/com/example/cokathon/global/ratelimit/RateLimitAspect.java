package com.example.cokathon.global.ratelimit;

import com.example.cokathon.global.exception.GlobalErrorCode;
import com.example.cokathon.global.exception.GlobalException;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RateLimitService rateLimitService;

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = generateKey(rateLimit.keyType());
        
        boolean allowed = rateLimitService.isAllowed(
            key, 
            rateLimit.maxRequests(), 
            Duration.ofSeconds(rateLimit.timeWindowSeconds())
        );
        
        if (!allowed) {
            throw GlobalException.from(GlobalErrorCode.TOO_MANY_REQUESTS);
        }
        
        return joinPoint.proceed();
    }

    private String generateKey(String keyType) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        
        if ("IP".equals(keyType)) {
            return getClientIpAddress(request);
        }
        
        return "default";
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}