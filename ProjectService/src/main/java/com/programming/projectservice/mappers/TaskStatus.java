package com.programming.projectservice.mappers;

import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class TaskStatus {

    @NotNull(message = "TASK ID name cannot be null")
    private Long taskId;


    @NotNull(message = "Status cannot be null")
    private String status;
}
