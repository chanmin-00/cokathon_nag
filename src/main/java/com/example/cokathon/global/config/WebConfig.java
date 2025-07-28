package com.example.cokathon.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("https://cokathon.r-e.kr") // 정확한 출처 명시
			.allowedOrigins("http://localhost:5173") // 로컬 개발 환경
			.allowedOrigins("http://localhost:8080") // 로컬 개발 환경
			.allowedOrigins("https://d2444j4wrmll2z.cloudfront.net") // CloudFront 배포 URL
			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
			.allowCredentials(true)
			.allowedHeaders("*");
	}
}
