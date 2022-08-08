package com.programming.projectservice.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.programming.projectservice.enums.Priority;
import com.programming.projectservice.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor @AllArgsConstructor
public class Task {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;


    private String description;


    @Enumerated(EnumType.STRING)
    private Status status;


    @Enumerated(EnumType.STRING)
    private Priority priority;

    //Assigne to User ID
    private Long assigneTo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;

    //TODO GITHUB FEATURE = link to a issue in github
    private String linkIssue;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sprint_id")
    @JsonIgnore
    private Sprint sprint;


    //First Created of task
    public Task(String name) {
        this.name = name;
        this.status = Status.TO_DO;
    }

}
