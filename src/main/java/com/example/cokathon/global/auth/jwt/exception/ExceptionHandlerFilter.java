package com.example.cokathon.global.auth.jwt.exception;

import static com.example.cokathon.global.exception.GlobalErrorCode.*;
import static com.example.cokathon.user.exception.UserErrorCode.*;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.cokathon.global.dto.ErrorResponse;
import com.example.cokathon.user.exception.UserException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);

		} catch (UserException e) {
			setErrorResponse(response, e.getHttpStatus().value(), e.getMessage(), e.getCode());

		} catch (ExpiredJwtException e) {
			setErrorResponse(response, 401, EXPIRED_TOKEN.getMessage(), EXPIRED_TOKEN.getCode());

		} catch (JwtException | IllegalArgumentException e) {
			setErrorResponse(response, 401, INVALID_TOKEN.getMessage(), INVALID_TOKEN.getCode());

		} catch (RuntimeException e) {
			setErrorResponse(response, 500, INTERNAL_SERVER_ERROR.getMessage(), INTERNAL_SERVER_ERROR.getCode());
		}
	}

	private void setErrorResponse(HttpServletResponse response, int status, String message, String code) {
		try {
			ErrorResponse errorResponse = ErrorResponse.of(
				HttpStatus.valueOf(status), message, code
			);

			response.setStatus(status);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
		} catch (IOException e) {
			log.error("Error writing error response: {}", e.getMessage());
		}
	}
}