package com.programming.projectservice.mappers;

import com.programming.projectservice.entities.Task;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SprintTask {

    private Long sprintId;

    private String sprintName;

    private List<Task> tasks = new ArrayList<>();

}
