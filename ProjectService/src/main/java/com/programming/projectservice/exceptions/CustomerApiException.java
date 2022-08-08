package com.programming.projectservice.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CustomerApiException {

    private String message;
    private HttpStatus httpStatus;
    private String throwable;
    private ZonedDateTime timestamp;

    private List<SubErrors> subErrorsList = new ArrayList<>();
    public CustomerApiException(String message, HttpStatus httpStatus, String throwable) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.throwable = throwable;
        this.timestamp = ZonedDateTime.now();
    }
}
