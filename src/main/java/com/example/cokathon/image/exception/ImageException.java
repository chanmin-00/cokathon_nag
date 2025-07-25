package com.example.cokathon.image.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ImageException extends RuntimeException {

	private final HttpStatus httpStatus;
	private final String code;

	private ImageException(String message, HttpStatus httpStatus, String code) {
		super(message);
		this.httpStatus = httpStatus;
		this.code = code;
	}

	public static ImageException from(ImageErrorCode errorCode) {
		return new ImageException(errorCode.getMessage(), errorCode.getHttpStatus(), errorCode.getCode());
	}
}
