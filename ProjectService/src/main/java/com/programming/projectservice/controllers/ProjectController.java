package com.programming.projectservice.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.programming.projectservice.dto.KafkaReportDTO;
import com.programming.projectservice.dto.UserProjectDTO;
import com.programming.projectservice.dto.UsersDTO;
import com.programming.projectservice.entities.Sprint;
import com.programming.projectservice.entities.Task;
import com.programming.projectservice.enums.Priority;
import com.programming.projectservice.enums.Status;
import com.programming.projectservice.exceptions.CustomerApiException;
import com.programming.projectservice.exceptions.DataAlreadyExists;
import com.programming.projectservice.exceptions.DataNotFound;
import com.programming.projectservice.feign.UserProjectClient;
import com.programming.projectservice.feign.UsersClient;
import com.programming.projectservice.mappers.*;
import com.programming.projectservice.services.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/project")
@AllArgsConstructor
public class
ProjectController {

    private UserProjectClient userProjectClient;

    private UsersClient usersClient;

    private ProjectService projectService;


    @GetMapping("/task/{taskId}")
    public ResponseEntity<Object> getTaskById(@PathVariable("taskId") Long taskId){
        Task task = projectService.getTaskById(taskId);

        return new ResponseEntity<>(task,HttpStatus.OK);
    }


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



    @GetMapping("/{projectId}/sprint/{sprintName}/tasks")
    public ResponseEntity<Object> getTaskByProjectSprint(@PathVariable("projectId") Long id,@PathVariable("sprintName") String name){
        if(projectService.checkSprintProject(id,name)){
            return new ResponseEntity<>(projectService.getTasksBySprintAndProject(name,id),HttpStatus.OK);
        }

        return new ResponseEntity<>(
                new CustomerApiException(
                        "No Tasks Found in this sprint",
                        HttpStatus.NOT_FOUND,
                        "I can't show you the Stack Trace 😅"),
                HttpStatus.NOT_FOUND);

    }



    @GetMapping("{projectId}/tasks")
    public ResponseEntity<Object> getTasksByProjectId(@PathVariable("projectId") Long id){
        if(projectService.findProjectById(id)==null){
            throw new DataNotFound("Project not found");
        }
        if(projectService.getTaskByProjectId(id)==null){
            throw new DataNotFound("No task found in this project");
        }

        return new ResponseEntity<>(projectService.getTaskByProjectId(id),HttpStatus.OK);
    }




    

    //Get Project Sprint and Task -> for backlog page
    @GetMapping("/{projectId}/sprints_tasks")
    public ResponseEntity<Object> getProjectSprintAndTask(@PathVariable("projectId") Long projectId){
        List<Sprint> sprints = projectService.getProjectSprints(projectId);
        List<SprintTask> sprintTasks = new ArrayList<>();
        sprints.forEach(sp->{
            SprintTask sprintTask = new SprintTask();
            sprintTask.setSprintId(sp.getId());
            sprintTask.setSprintName(sp.getName());
            if(projectService.getTasksBySprintAndProject(sp.getName(),projectId)==null)
                sprintTask.setTasks(null);

            sprintTask.setTasks(projectService.getTasksBySprintAndProject(sp.getName(),projectId));

            sprintTasks.add(sprintTask);

        });

        return new ResponseEntity<>(sprintTasks,HttpStatus.OK);
    }


    //Get Sprint by project also get tasks :)
    @GetMapping("/{projectId}/sprints")
    public ResponseEntity<Object> getProjectSprint(@PathVariable("projectId") Long projectId){
        return new ResponseEntity<>(projectService.getProjectSprints(projectId),HttpStatus.OK);
    }


    @PostMapping("/{projectId}/sprint/{sprintId}/task/{taskId}")
    @Transactional
    public ResponseEntity<Object> changeTaskSprint(@PathVariable("projectId") Long projectId,@PathVariable("sprintId") Long sprintId,@PathVariable("taskId") Long taskId){
        Task task = projectService.getTaskById(taskId);
        if(Objects.equals(task.getSprint().getId(), sprintId))
            throw new DataAlreadyExists("This task is in this sprint");

        Sprint newSp = projectService.getSprintById(sprintId);
        task.setSprint(newSp);
        projectService.addTask(task);

        return new ResponseEntity<>(task,HttpStatus.ACCEPTED);
    }





    @Transactional
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



    //Add task to sprint
    @PostMapping("/task")
    public ResponseEntity<Object> addTask(@Valid @RequestBody TaskNameMapper taskMapper){

        Task task = new Task(taskMapper.getName());

        if(projectService.checkSprintProject(taskMapper.getProjectId(),taskMapper.getSprintName())){

            task.setSprint(projectService.getSprintByNameAndProjectId(taskMapper.getSprintName(),taskMapper.getProjectId()));

            projectService.addTask(task);

        }

        return new ResponseEntity<>(task,HttpStatus.CREATED);

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

        if(ts.getName()!=null)
            task.setName(ts.getName());

        if(ts.getDescription()!=null)
            task.setDescription(ts.getDescription());

        if(ts.getLinkIssue()!=null)
            task.setLinkIssue(ts.getLinkIssue());

        if(ts.getDueDate()!=null)
            task.setDueDate(ts.getDueDate());

        task.setAssigneTo(ts.getAssigneTo());

        if(ts.getStatus()!=null)
            task.setStatus(Status.valueOf(ts.getStatus()));
        else
            task.setStatus(Status.TO_DO);

        System.out.println("Priority "+ts.getPriority());
        if(ts.getPriority()==null)
            task.setPriority(null);
        else
            task.setPriority(Priority.valueOf(ts.getPriority()));

        projectService.addTask(task);

        return new ResponseEntity<>(task, HttpStatus.OK);
    }



    @PostMapping("/sendHistoryNotification")
    public ResponseEntity<Object> sendHistoryToNotificationService(@RequestBody KafkaReportDTO kafkaReportDTO) throws JsonProcessingException {
        return new ResponseEntity<>(projectService.send(kafkaReportDTO),HttpStatus.OK);
    }
}
