package com.example.cokathon.global.auth.jwt.exception;

import static com.example.cokathon.user.exception.UserErrorCode.*;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.example.cokathon.global.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
			throws IOException {
		ErrorResponse errorResponse = ErrorResponse.of(
			FORBIDDEN_ACCESS.getHttpStatus(),
			FORBIDDEN_ACCESS.getMessage(),
			FORBIDDEN_ACCESS.getCode()
		);

		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
	}
}
