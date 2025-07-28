package com.example.cokathon.global.ratelimit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

import static com.example.cokathon.global.exception.GlobalErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.cokathon.global.exception.GlobalException;

@ExtendWith(MockitoExtension.class)
class RateLimitAspectTest {

    @Mock
    private RateLimitService rateLimitService;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @InjectMocks
    private RateLimitAspect rateLimitAspect;

    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    @DisplayName("처리율 제한 내에서는 정상적으로 메서드가 실행된다")
    void withinRateLimit_ShouldProceedMethod() throws Throwable {
        RateLimit rateLimit = createRateLimit(5, 300L, "IP");
        request.setRemoteAddr("127.0.0.1");
        
        when(rateLimitService.isAllowed(eq("127.0.0.1"), eq(5), eq(Duration.ofSeconds(300)))).thenReturn(true);
        when(joinPoint.proceed()).thenReturn("success");

        Object result = rateLimitAspect.rateLimit(joinPoint, rateLimit);

        assertThat(result).isEqualTo("success");
        verify(joinPoint).proceed();
    }

    @Test
    @DisplayName("처리율 제한 초과시 GlobalException이 발생한다")
    void exceedsRateLimit_ShouldThrowException() throws Throwable {
        RateLimit rateLimit = createRateLimit(5, 300L, "IP");
        request.setRemoteAddr("127.0.0.1");
        
        when(rateLimitService.isAllowed(eq("127.0.0.1"), eq(5), eq(Duration.ofSeconds(300)))).thenReturn(false);

        assertThatThrownBy(() -> rateLimitAspect.rateLimit(joinPoint, rateLimit))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(TOO_MANY_REQUESTS.getMessage());

        verify(joinPoint, never()).proceed();
    }

    @Test
    @DisplayName("X-Forwarded-For 헤더가 있을 때 올바른 IP를 추출한다")
    void withXForwardedForHeader_ShouldExtractCorrectIP() throws Throwable {
        RateLimit rateLimit = createRateLimit(5, 300L, "IP");
        request.addHeader("X-Forwarded-For", "192.168.1.1, 10.0.0.1");
        request.setRemoteAddr("127.0.0.1");
        
        when(rateLimitService.isAllowed(eq("192.168.1.1"), eq(5), eq(Duration.ofSeconds(300)))).thenReturn(true);
        when(joinPoint.proceed()).thenReturn("success");

        rateLimitAspect.rateLimit(joinPoint, rateLimit);

        verify(rateLimitService).isAllowed(eq("192.168.1.1"), eq(5), eq(Duration.ofSeconds(300)));
    }

    @Test
    @DisplayName("X-Real-IP 헤더가 있을 때 올바른 IP를 추출한다")
    void withXRealIPHeader_ShouldExtractCorrectIP() throws Throwable {
        RateLimit rateLimit = createRateLimit(5, 300L, "IP");
        request.addHeader("X-Real-IP", "192.168.1.100");
        request.setRemoteAddr("127.0.0.1");
        
        when(rateLimitService.isAllowed(eq("192.168.1.100"), eq(5), eq(Duration.ofSeconds(300)))).thenReturn(true);
        when(joinPoint.proceed()).thenReturn("success");

        rateLimitAspect.rateLimit(joinPoint, rateLimit);

        verify(rateLimitService).isAllowed(eq("192.168.1.100"), eq(5), eq(Duration.ofSeconds(300)));
    }

    private RateLimit createRateLimit(int maxRequests, long timeWindowSeconds, String keyType) {
        return new RateLimit() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return RateLimit.class;
            }

            @Override
            public int maxRequests() {
                return maxRequests;
            }

            @Override
            public long timeWindowSeconds() {
                return timeWindowSeconds;
            }

            @Override
            public String keyType() {
                return keyType;
            }
        };
    }
}