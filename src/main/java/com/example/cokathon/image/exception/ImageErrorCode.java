package com.example.cokathon.image.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageErrorCode {

	IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "이미지를 찾을 수 없습니다.", "IMAGE_ERROR_404_NOT_FOUND"),
	IMAGE_PROCESSING_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 처리에 실패했습니다.", "IMAGE_ERROR_500_PROCESSING_FAIL");

	private final HttpStatus httpStatus;
	private final String message;
	private final String code;

}
