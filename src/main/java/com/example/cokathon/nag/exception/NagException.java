package com.example.cokathon.nag.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class NagException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String code;

    private NagException(String message, HttpStatus httpStatus, String code) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public static com.example.cokathon.nag.exception.NagException from(NagErrorCode errorCode) {
        return new com.example.cokathon.nag.exception.NagException(errorCode.getMessage(), errorCode.getHttpStatus(), errorCode.getCode());
    }
}

