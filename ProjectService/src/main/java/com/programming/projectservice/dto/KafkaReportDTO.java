package com.programming.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
public class KafkaReportDTO {

    private String event;

    //Like Aymen Chikeb change the status : Done -> IN PROGRESS
    private String description;

    private String userId;

    private String projectId;

    private String projectName;

    private Integer phoneNumber;

    private String userEmail;

    private String userAvatar;
}
