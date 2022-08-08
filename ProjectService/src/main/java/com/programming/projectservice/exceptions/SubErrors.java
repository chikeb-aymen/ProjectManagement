package com.programming.projectservice.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubErrors {
    private String field;

    private String message;

    private String objectName;

    private Object rejectedValue;


}
