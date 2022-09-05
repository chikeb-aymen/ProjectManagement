package com.programming.authservice.exceptions;

public class DataAlreadyExists extends RuntimeException{
    public DataAlreadyExists(String message) {
        super(message);
    }
}
