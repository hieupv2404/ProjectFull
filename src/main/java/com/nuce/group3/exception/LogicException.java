package com.nuce.group3.exception;

import org.springframework.http.HttpStatus;

public class LogicException extends Exception {
    private HttpStatus httpStatus;

    public LogicException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
