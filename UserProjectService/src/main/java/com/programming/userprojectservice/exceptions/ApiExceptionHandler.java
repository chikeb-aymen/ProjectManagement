package com.programming.userprojectservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(value = {DataNotFoundException.class})
    public ResponseEntity<Object> handleDataNotFound(DataNotFoundException e){

        CustomerApiException apiException = new CustomerApiException(e.getMessage(), HttpStatus.NOT_FOUND,"I can not show you Stack Trace 😅");

        return new ResponseEntity<>(apiException,HttpStatus.NOT_FOUND);
    }



}
