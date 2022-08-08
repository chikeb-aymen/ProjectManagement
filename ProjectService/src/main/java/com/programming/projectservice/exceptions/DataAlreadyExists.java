package com.programming.projectservice.exceptions;

public class DataAlreadyExists extends RuntimeException{
    public DataAlreadyExists(String message) {
        super(message);
    }
}
