package com.example.cokathon.nag.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NagErrorCode {

    NAG_NOT_FOUND(HttpStatus.NOT_FOUND, "잔소리를 찾을 수 없습니다.", "NAG_ERROR_404_NOT_FOUND"),
    NAG_DELETED(HttpStatus.INTERNAL_SERVER_ERROR, "영구삭제된 잔소리입니다.", "NAG_ERROR_500_PROCESSING_FAIL");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;

}