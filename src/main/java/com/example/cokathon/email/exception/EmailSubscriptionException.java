package com.example.cokathon.email.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class EmailSubscriptionException extends RuntimeException {

	private final HttpStatus httpStatus;
	private final String code;

	private EmailSubscriptionException(String message, HttpStatus httpStatus, String code) {
		super(message);
		this.httpStatus = httpStatus;
		this.code = code;
	}

	public static EmailSubscriptionException from(EmailSubscriptionErrorCode errorCode) {
		return new EmailSubscriptionException(errorCode.getMessage(), errorCode.getHttpStatus(), errorCode.getCode());
	}
}