package com.programming.projectservice.controllers;


import com.programming.projectservice.dto.UserProjectDTO;
import com.programming.projectservice.dto.UsersDTO;
import com.programming.projectservice.entities.Sprint;
import com.programming.projectservice.entities.Task;
import com.programming.projectservice.enums.Priority;
import com.programming.projectservice.enums.Status;
import com.programming.projectservice.exceptions.CustomerApiException;
import com.programming.projectservice.exceptions.DataNotFound;
import com.programming.projectservice.feign.UserProjectClient;
import com.programming.projectservice.feign.UsersClient;
import com.programming.projectservice.mappers.SprintMapper;
import com.programming.projectservice.mappers.TaskMapper;
import com.programming.projectservice.mappers.TaskNameMapper;
import com.programming.projectservice.mappers.TaskStatus;
import com.programming.projectservice.services.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/project")
@AllArgsConstructor
public class ProjectController {

    private UserProjectClient userProjectClient;

    private UsersClient usersClient;

    private ProjectService projectService;


    @GetMapping("/{projectId}/users")
    public ResponseEntity<Object> listUsersByProject(@PathVariable("projectId") Long projectId){
        List<UserProjectDTO> projectUsersId = userProjectClient.getUsersByProject(projectId);

        if(projectUsersId.size()<=0){
            throw new DataNotFound("There are no people in this project");
        }

        List<UsersDTO> projectUsers = new ArrayList<>();

        for (UserProjectDTO pu:projectUsersId) {
            projectUsers.add(usersClient.getUserDetails(pu.getUserId()));
        }

        return new ResponseEntity<>(projectUsers, HttpStatus.OK);
    }


    @PostMapping("/sprint")
    public ResponseEntity<Object> addSprint(@RequestBody SprintMapper sprintMapper){
        Sprint sprint = new Sprint();

        if(!projectService.sprintNameExists(sprintMapper.getName())){

            sprint.setName(sprintMapper.getName());
            sprint.setStartDate(sprintMapper.getStartDate());
            sprint.setEndDate(sprintMapper.getEndDate());
            sprint.setProject(projectService.findProjectById(sprintMapper.getProjectId()));
            projectService.addSprint(sprint);

        }
        return new ResponseEntity<>(sprint,HttpStatus.CREATED);

    }


    @PostMapping("/task")
    public ResponseEntity<Object> addTask(@Valid @RequestBody TaskNameMapper taskMapper){

            Task task = new Task(taskMapper.getName());

            if(projectService.checkSprintProject(taskMapper.getProjectId(),taskMapper.getSprintName())){

                task.setSprint(projectService.getSprintByNameAndProjectId(taskMapper.getSprintName(),taskMapper.getProjectId()));

                projectService.addTask(task);

            }

            return new ResponseEntity<>(task,HttpStatus.CREATED);
    }

    @GetMapping("/{projectId}/sprint/{sprintName}/tasks")
    public ResponseEntity<Object> getTaskByProjectSprint(@PathVariable("projectId") Long id,@PathVariable("sprintName") String name){
        if(projectService.checkSprintProject(id,name)){
            return new ResponseEntity<>(projectService.getTasksBySprintAndProject(name,id),HttpStatus.OK);
        }

        return new ResponseEntity<>(
                new CustomerApiException(
                        "No Tasks Found in this sprint",
                        HttpStatus.NOT_FOUND,
                        "I can not show you Stack Trace 😅"),
                HttpStatus.NOT_FOUND);

    }


    @PostMapping("/task/updateStatus")
    @Transactional
    public ResponseEntity<Object> updateTaskStatus(@Valid @RequestBody TaskStatus taskStatus){

        Task task = projectService.getTaskById(taskStatus.getTaskId());
        Status newStatus = Status.valueOf(taskStatus.getStatus());
        task.setStatus(newStatus);

        projectService.addTask(task);

        return new ResponseEntity<>(task,HttpStatus.OK);
    }


    @PostMapping("/task/{taskId}/update")
    public ResponseEntity<Object> updateTask(@PathVariable("taskId") Long taskId,@RequestBody TaskMapper ts){
        Task task = projectService.getTaskById(taskId);

        if(task==null){
            throw new DataNotFound("Task not found");
        }

        task.setName(ts.getName());
        task.setDescription(ts.getDescription());
        task.setLinkIssue(ts.getLinkIssue());
        task.setDueDate(ts.getDueDate());
        task.setAssigneTo(ts.getAssigneTo());
        task.setStatus(Status.valueOf(ts.getStatus()));
        task.setPriority(Priority.valueOf(ts.getPriority()));

        projectService.addTask(task);

        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    //TODO assigne TO User
}
