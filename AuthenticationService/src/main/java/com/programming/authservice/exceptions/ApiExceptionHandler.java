package com.programming.authservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({DataNotFound.class})
    public ResponseEntity<Object> handlerDataNotFound(DataNotFound e){
        CustomerApiException apiException = new CustomerApiException(e.getMessage(), HttpStatus.NOT_FOUND,"I can not show you Stack Trace 😅");

        return new ResponseEntity<>(apiException,HttpStatus.NOT_FOUND);
    }
}
