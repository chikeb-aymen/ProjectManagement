package com.programming.projectservice.mappers;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TaskNameMapper {


    @NotNull(message = "Name cannot be null")
    private String name;

    @NotNull(message = "SprintId cannot be null")
    private Long sprintId;

}
