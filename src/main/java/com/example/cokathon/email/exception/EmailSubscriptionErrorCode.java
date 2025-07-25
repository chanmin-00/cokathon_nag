package com.example.cokathon.email.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailSubscriptionErrorCode {

	SUBSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "구독 정보를 찾을 수 없습니다.", "EMAIL_SUBSCRIPTION_ERROR_404_NOT_FOUND"),

	INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 이메일 형식입니다.", "EMAIL_SUBSCRIPTION_ERROR_400_INVALID_FORMAT"),
	SEND_EMAIL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송 중 오류가 발생했습니다.",
		"EMAIL_SUBSCRIPTION_ERROR_500_SEND_ERROR");

	private final HttpStatus httpStatus;
	private final String message;
	private final String code;
}