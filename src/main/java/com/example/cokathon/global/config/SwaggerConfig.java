package com.example.cokathon.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		SecurityScheme securityScheme = new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT")
			.in(SecurityScheme.In.HEADER)
			.name("Authorization");

		SecurityRequirement securityRequirement = new SecurityRequirement().addList("BearerAuth");

		Server prodServer = new Server()
			.url("https://cokathon.r-e.kr")
			.description("운영 서버");

		Server localServer = new Server()
			.url("http://localhost:8080")
			.description("로컬 개발 서버");

		return new OpenAPI()
			.components(new Components().addSecuritySchemes("BearerAuth", securityScheme))
			.info(new Info()
				.title("cokathon REST API")
				.description("cokathon Swagger")
				.version("1.0.0"))
			.addSecurityItem(securityRequirement)
			.servers(List.of(prodServer, localServer)); // ✅ 서버 드롭다운 표시용
	}
}
