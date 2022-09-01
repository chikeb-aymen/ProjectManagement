package com.programming.projectservice.exceptions;

import feign.FeignException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<SubErrors> subErrors = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()){

            String fieldName = ((FieldError) error).getField();
            String objectName = error.getObjectName();
            Object rejectedValue = ((FieldError) error).getRejectedValue();
            String message = error.getDefaultMessage();

            subErrors.add( new SubErrors(fieldName,message,objectName,rejectedValue));
        }

        CustomerApiException apiException = new CustomerApiException("Argument Not Valid",HttpStatus.NOT_ACCEPTABLE,"I can't show you the Stack Trace 😅");

        apiException.setSubErrorsList(subErrors);

        return new ResponseEntity<>(apiException,HttpStatus.NOT_ACCEPTABLE);
    }


    @ExceptionHandler(FeignException.class)
    protected ResponseEntity<Object> handleFeignStatusException(FeignException ex, HttpServletResponse response){
        System.out.println(ex.getMessage());
        CustomerApiException apiException = new CustomerApiException("User not found in platform", HttpStatus.NOT_FOUND,"I can't show you the Stack Trace 😅");
        return new ResponseEntity<>(apiException,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({DataNotFound.class})
    public ResponseEntity<Object> handlerDataNotFound(DataNotFound e){
        CustomerApiException apiException = new CustomerApiException(e.getMessage(), HttpStatus.NOT_FOUND,"I can't show you the Stack Trace 😅");

        return new ResponseEntity<>(apiException,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({DataAlreadyExists.class})
    public ResponseEntity<Object> handlerDataExists(DataAlreadyExists e){
        CustomerApiException apiException = new CustomerApiException(e.getMessage(), HttpStatus.FOUND,"I can't show you the Stack Trace 😅");

        return new ResponseEntity<>(apiException,HttpStatus.FOUND);
    }
}
