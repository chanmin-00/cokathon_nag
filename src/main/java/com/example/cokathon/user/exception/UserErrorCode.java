package com.example.cokathon.user.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode {

	// 사용자 관련 예외 코드

	// 404 Not Found
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", "USER_ERROR_404_NOT_FOUND"),
	USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다.", "USER_ERROR_409_ALREADY_EXISTS"),

	// 401 Unauthorized
	INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "잘못된 비밀번호입니다.", "USER_ERROR_401_INVALID_PASSWORD"),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.", "USER_ERROR_401_INVALID_TOKEN"),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다.", "USER_ERROR_401_INVALID_REFRESH_TOKEN"),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다.", "USER_ERROR_401_EXPIRED_TOKEN"),
	UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "권한이 없는 접근입니다.", "USER_ERROR_401_UNAUTHORIZED_ACCESS"),

	// 403 Forbidden
	FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "접근이 금지되었습니다.", "USER_ERROR_403_FORBIDDEN_ACCESS");

	private final HttpStatus httpStatus;
	private final String message;
	private final String code;

}