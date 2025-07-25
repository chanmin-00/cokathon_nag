package com.example.cokathon.global.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.cokathon.global.dto.ErrorResponse;
import com.example.cokathon.image.exception.ImageException;
import com.example.cokathon.user.exception.UserException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException e) {
		Map<String, String> errors = new HashMap<>();
		e.getBindingResult().getAllErrors()
			.forEach(error -> errors.put(((FieldError)error).getField(), error.getDefaultMessage()));

		log.error("MethodArgumentNotValidException: {}", errors);

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ErrorResponse.of(GlobalErrorCode.INVALID_PARAMETER, errors));
	}

	@ExceptionHandler(ImageException.class)
	public ResponseEntity<Object> handleImageException(ImageException e) {
		log.error("ImageException: {}", e.getMessage(), e);

		return ResponseEntity
			.status(e.getHttpStatus())
			.body(ErrorResponse.of(e.getHttpStatus(), e.getMessage(), e.getCode()));
	}

	@ExceptionHandler(UserException.class)
	public ResponseEntity<Object> handleUserException(UserException e) {
		log.error("UserException: {}", e.getMessage(), e);

		return ResponseEntity
			.status(e.getHttpStatus())
			.body(ErrorResponse.of(e.getHttpStatus(), e.getMessage(), e.getCode()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleAllExceptions(Exception e) {
		log.error("Unhandled exception: {}", e.getMessage(), e);

		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ErrorResponse.from(GlobalErrorCode.INTERNAL_SERVER_ERROR));
	}

}
