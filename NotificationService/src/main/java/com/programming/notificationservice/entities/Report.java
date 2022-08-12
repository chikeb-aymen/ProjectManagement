package com.programming.notificationservice.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
@AllArgsConstructor @NoArgsConstructor
public class Report {

    @Id
    private String reportId;

    private String event;

    //Like Aymen Chikeb change the status : Done -> IN PROGRESS
    private String description;

    private String userId;

    private String userEmail;

    private String userAvatar;

    private LocalDateTime creationDate = LocalDateTime.now();



}
