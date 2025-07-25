package com.example.cokathon.global.auth.jwt.exception;

import static com.example.cokathon.user.exception.UserErrorCode.*;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.example.cokathon.global.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
			throws IOException {
		ErrorResponse errorResponse = ErrorResponse.of(
			UNAUTHORIZED_ACCESS.getHttpStatus(),
			UNAUTHORIZED_ACCESS.getMessage(),
			UNAUTHORIZED_ACCESS.getCode()
		);

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
	}
}

