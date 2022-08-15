package com.programming.projectservice.controllers;

import com.programming.projectservice.dto.UserProjectDTO;
import com.programming.projectservice.dto.UsersDTO;
import com.programming.projectservice.entities.Task;
import com.programming.projectservice.enums.Priority;
import com.programming.projectservice.enums.Status;
import com.programming.projectservice.exceptions.DataNotFound;
import com.programming.projectservice.feign.UserProjectClient;
import com.programming.projectservice.feign.UsersClient;
import com.programming.projectservice.mappers.UserTaskStatus;
import com.programming.projectservice.services.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/project/{projectId}/statistics")
@AllArgsConstructor
public class ProjectStaticsController {

    private UserProjectClient userProjectClient;

    private ProjectService projectService;

    private UsersClient usersClient;

    //Get status percentage
    @GetMapping("/status/percentage")
    public ResponseEntity<Object> getStatusPercentage(@PathVariable("projectId") Long projectId){
        //TODO getTaskByProjectId without backlog task
        if(projectService.getTaskByProjectId(projectId)==null)
            throw new DataNotFound("No tasks found in this project");

        //to do, in progress, done, complete
        int[] statusCount = new int[]{0,0,0,0};
        int[] statusPercentage = new int[]{0,0,0,0};

        List<Task> taskList = projectService.getTaskByProjectId(projectId);
        taskList.forEach(task ->{
            if(Status.TO_DO.equals(task.getStatus())){
                statusCount[0] += 1;
            }
            else if(Status.IN_PROGRESS.equals(task.getStatus())){
                statusCount[1] += 1;
            }
            else if(Status.DONE.equals(task.getStatus())){
                statusCount[2] += 1;
            }
            else if(Status.COMPLETE.equals(task.getStatus())){
                statusCount[3] += 1;
            }

        });

        for (int i = 0; i < statusCount.length; i++) {
            statusPercentage[i] = ((statusCount[i] * 100)/ taskList.size());
        }

        return new ResponseEntity<>(statusPercentage, HttpStatus.OK);
    }

    //Get priority percentage
    @GetMapping("/priority/percentage")
    public ResponseEntity<Object> getPriorityPercentage(@PathVariable("projectId") Long projectId){
        //TODO getTaskByProjectId without backlog task
        if(projectService.getTaskByProjectId(projectId)==null)
            throw new DataNotFound("No tasks found in this project");

        //Lowest, Low, Medium, High, Highest
        int[] priorityCount = new int[]{0,0,0,0,0};

        List<Task> taskList = projectService.getTaskByProjectId(projectId);

        taskList.forEach(task ->{
            if(Priority.Lowest.equals(task.getPriority())){
                priorityCount[0] += 1;
            }
            else if(Priority.Low.equals(task.getPriority())){
                priorityCount[1] += 1;
            }
            else if(Priority.Medium.equals(task.getPriority())){
                priorityCount[2] += 1;
            }
            else if(Priority.High.equals(task.getPriority())){
                priorityCount[3] += 1;
            }
            else if(Priority.Highest.equals(task.getPriority())){
                priorityCount[4] += 1;
            }

        });


        return new ResponseEntity<>(priorityCount, HttpStatus.OK);
    }

    //Get People Task [username, {done:1,to do: 1,....}]
    @GetMapping("/userTaskStatus/percentage")
    public ResponseEntity<Object> getPercentagePeopleTask(@PathVariable("projectId") Long projectId){

        List<UserProjectDTO> projectUsersId = userProjectClient.getUsersByProject(projectId);

        if(projectUsersId.size()<=0){
            throw new DataNotFound("There are no people in this project");
        }

        List<UsersDTO> projectUsers = new ArrayList<>();

        for (UserProjectDTO pu:projectUsersId) {
            projectUsers.add(usersClient.getUserDetails(pu.getUserId()));
        }

        List<UserTaskStatus> userTaskStatusList = new ArrayList<>();

        projectUsers.forEach(prU->{
            UserTaskStatus userTaskStatus = new UserTaskStatus();
            userTaskStatus.setUsername(prU.getUsername());

            //Get All Task By UserId
            List<Task> userTask = projectService.getTaskByUserAssigned(prU.getId());

            Map<String,Integer> initializeTaskStatus = initializeTaskStatus();

            userTask.forEach(t->{
                if(t.getStatus().equals(Status.TO_DO)){
                    initializeTaskStatus.put("TODO",initializeTaskStatus.get("TODO")+1);
                }else if(t.getStatus().equals(Status.IN_PROGRESS)){
                    initializeTaskStatus.put("IN PROGRESS",initializeTaskStatus.get("IN PROGRESS")+1);
                }else if(t.getStatus().equals(Status.DONE)){
                    initializeTaskStatus.put("DONE",initializeTaskStatus.get("DONE")+1);
                }else if(t.getStatus().equals(Status.COMPLETE)){
                    initializeTaskStatus.put("COMPLETE",initializeTaskStatus.get("COMPLETE")+1);
                }
            });

            userTaskStatus.setTaskStatus(initializeTaskStatus);

            userTaskStatusList.add(userTaskStatus);

        });


        return new ResponseEntity<>(userTaskStatusList,HttpStatus.OK);


    }


    Map<String,Integer> initializeTaskStatus(){
        Map<String,Integer> initializeTaskStatus = new HashMap<>();

        initializeTaskStatus.put("TODO",0);
        initializeTaskStatus.put("IN PROGRESS",0);
        initializeTaskStatus.put("DONE",0);
        initializeTaskStatus.put("COMPLETE",0);

        return initializeTaskStatus;
    }
}
