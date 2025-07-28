package com.example.cokathon.nag.controller;

import com.example.cokathon.global.ratelimit.RateLimit;
import com.example.cokathon.nag.enums.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * NagController의 Rate limit 어노테이션이 올바르게 적용되었는지 확인하는 간단한 테스트
 */
class NagControllerSimpleTest {

    @Test
    @DisplayName("NagController의 createNag 메서드에 @RateLimit 어노테이션이 적용되어 있는지 확인")
    void createNag_HasRateLimitAnnotation() throws NoSuchMethodException {
        // Given
        Class<NagController> controllerClass = NagController.class;
        
        // When
        Method createNagMethod = controllerClass.getDeclaredMethod("createNag", 
            com.example.cokathon.nag.dto.request.NagCreateRequest.class);
        
        // Then
        assertThat(createNagMethod.isAnnotationPresent(RateLimit.class)).isTrue();
        
        RateLimit rateLimit = createNagMethod.getAnnotation(RateLimit.class);
        assertThat(rateLimit.maxRequests()).isEqualTo(5);
        assertThat(rateLimit.timeWindowSeconds()).isEqualTo(300);
        assertThat(rateLimit.keyType()).isEqualTo("IP");
    }

    @Test
    @DisplayName("NagController의 createNag 메서드에 @PostMapping 어노테이션이 적용되어 있는지 확인")
    void createNag_HasPostMappingAnnotation() throws NoSuchMethodException {
        // Given
        Class<NagController> controllerClass = NagController.class;
        
        // When
        Method createNagMethod = controllerClass.getDeclaredMethod("createNag", 
            com.example.cokathon.nag.dto.request.NagCreateRequest.class);
        
        // Then
        assertThat(createNagMethod.isAnnotationPresent(PostMapping.class)).isTrue();
    }

    @Test
    @DisplayName("Category enum에 필요한 값들이 존재하는지 확인")
    void category_HasRequiredValues() {
        // Given & When
        Category[] categories = Category.values();
        
        // Then
        assertThat(categories).isNotEmpty();
        assertThat(categories).contains(Category.자취);
        assertThat(categories).contains(Category.직장);
        assertThat(categories).contains(Category.육아);
    }

    @Test
    @DisplayName("처리율 제한 설정값 확인")
    void rateLimitConfiguration_IsCorrect() throws NoSuchMethodException {
        // Given
        Method createNagMethod = NagController.class.getDeclaredMethod("createNag", 
            com.example.cokathon.nag.dto.request.NagCreateRequest.class);
        RateLimit rateLimit = createNagMethod.getAnnotation(RateLimit.class);
        
        // When & Then
        assertThat(rateLimit.maxRequests()).isEqualTo(5); // 5분간 5회 제한
        assertThat(rateLimit.timeWindowSeconds()).isEqualTo(300); // 5분 (300초)
        assertThat(rateLimit.keyType()).isEqualTo("IP"); // IP 기반
        
        System.out.println("✅ 처리율 제한 설정:");
        System.out.println("  - 최대 요청 수: " + rateLimit.maxRequests() + "회");
        System.out.println("  - 시간 윈도우: " + rateLimit.timeWindowSeconds() + "초 (" + (rateLimit.timeWindowSeconds()/60) + "분)");
        System.out.println("  - 키 타입: " + rateLimit.keyType());
    }
}