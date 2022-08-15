package com.programming.projectservice.mappers;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * I make this class for return the task status for each of users
 */
@Data
public class UserTaskStatus {

    private String username;

    private Map<String,Integer> taskStatus = new HashMap<>();

}
