package com.programming.projectservice.mappers;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class TaskMapper {

    private Long id;

    private String name;


    private String description;


    private String status;


    private String priority;

    //Assigne to User ID
    private Long assigneTo;

    private LocalDateTime dueDate;


    private String linkIssue;
}
