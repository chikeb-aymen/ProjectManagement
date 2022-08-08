package com.programming.authservice.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
public class CustomerApiException {

    private String message;
    private HttpStatus httpStatus;
    private String throwable;
    private ZonedDateTime timestamp;

    public CustomerApiException(String message, HttpStatus httpStatus, String throwable) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.throwable = throwable;
        this.timestamp = ZonedDateTime.now();
    }
}
