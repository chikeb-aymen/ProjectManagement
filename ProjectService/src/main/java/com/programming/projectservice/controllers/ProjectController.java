package com.programming.projectservice.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.programming.projectservice.dto.KafkaReportDTO;
import com.programming.projectservice.dto.UserProjectDTO;
import com.programming.projectservice.dto.UsersDTO;
import com.programming.projectservice.entities.Project;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/project")
@AllArgsConstructor
public class ProjectController {

    private UserProjectClient userProjectClient;

    private UsersClient usersClient;

    private ProjectService projectService;


    @GetMapping("/task/{taskId}")
    public ResponseEntity<Object> getTaskById(@PathVariable("taskId") Long taskId){
        Task task = projectService.getTaskById(taskId);

        return new ResponseEntity<>(task,HttpStatus.OK);
    }


    @GetMapping("/{projectId}/users")
    public ResponseEntity<Object> listUsersByProject(@PathVariable("projectId") Long projectId,@RequestHeader("Authorization") String authorization){
        List<UserProjectDTO> projectUsersId = userProjectClient.getUsersByProject(projectId);

        if(projectUsersId.size()<=0){
            throw new DataNotFound("There are no people in this project");
        }

        List<UsersDTO> projectUsers = new ArrayList<>();

        for (UserProjectDTO pu:projectUsersId) {
            projectUsers.add(usersClient.getUserDetails(pu.getUserId(),authorization));
        }

        return new ResponseEntity<>(projectUsers, HttpStatus.OK);
    }


    @GetMapping("/user/{userId}/projects")
    public ResponseEntity<Object> listProjectsByUser(@PathVariable("userId") Long userId){
        List<UserProjectDTO> projectUsersId = userProjectClient.getUserProjects(userId);

        if(projectUsersId.size()<=0){
            throw new DataNotFound("There are no project for you");
        }

        List<Project> projects = new ArrayList<>();

        for (UserProjectDTO pu:projectUsersId) {
            projects.add(projectService.findProjectById(pu.getProjectId()));
        }

        return new ResponseEntity<>(projects, HttpStatus.OK);
    }



    @GetMapping("/{projectId}")
    public ResponseEntity<Object> getProjectById(@PathVariable("projectId") Long projectId){
        return new ResponseEntity<>(projectService.findProjectById(projectId),HttpStatus.OK);
    }



    //(backlog) -> Get tasks that are not in sprints (sprint backlog)
    @GetMapping("/{projectId}/backlog")
    public ResponseEntity<Object> getBacklogTask(@PathVariable("projectId") Long projectId){

       return new ResponseEntity<>( projectService.getSprintByNameContainAndProjectId("Backlog",projectId),HttpStatus.OK);
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



    //Old version of project
    @GetMapping("/{projectId}/tasks")
    public ResponseEntity<Object> getTasksByProjectId(@PathVariable("projectId") Long id){
        if(projectService.findProjectById(id)==null){
            throw new DataNotFound("Project not found");
        }
        if(projectService.getTaskByProjectId(id)==null){
            throw new DataNotFound("No task found in this project");
        }

        return new ResponseEntity<>(projectService.getTaskByProjectId(id),HttpStatus.OK);
    }


    @GetMapping("/{projectId}/tasks/sprint_started")
    public ResponseEntity<Object> getTasksBySprintStartedInProjectId(@PathVariable("projectId") Long id){
        if(projectService.findProjectById(id)==null){
            throw new DataNotFound("Project not found");
        }
        if(projectService.getTaskByProjectId(id)==null){
            throw new DataNotFound("No task found in this project");
        }

        if(projectService.getSprintStartedInProject(id)==null)
            throw new DataNotFound("Task don't found yet");

        Sprint sp = projectService.getSprintStartedInProject(id);

        return new ResponseEntity<>(sp.getTasks(),HttpStatus.OK);
    }


    //Project Has Sprint Started
    @GetMapping("/{projectId}/hasSprintStarted")
    public boolean projectHasSprintStarted(@PathVariable("projectId") Long projectId){
        return projectService.hasSprintStarted(projectId);
    }

    //Started Sprint
    @PostMapping("/{projectId}/sprint/{sprintId}/start")
    public ResponseEntity<Object> startSprint(@PathVariable("projectId") Long projectId,@PathVariable("sprintId") Long sprintId){
        if(projectService.hasSprintStarted(projectId))
            throw new DataAlreadyExists("Project has sprint already started");

        if(projectService.getSprintById(sprintId)==null)
            throw new DataNotFound("This sprint not exists in this project");

        Sprint sp = projectService.getSprintById(sprintId);

        sp.setStarted(true);

        projectService.addSprint(sp);


        return new ResponseEntity<>("Sprint has been started successfully",HttpStatus.OK);
    }


    @PostMapping("/{projectId}/sprint/{sprintId}/complete/{toSprint}")
    public ResponseEntity<Object> completeSprint(@PathVariable("projectId") Long projectId,
                                                 @PathVariable("sprintId") Long sprintId,
                                                 @PathVariable("toSprint") Long toSprint){
        Sprint sp = projectService.getSprintByIdAndProjectId(sprintId,projectId);
        sp.setComplete(true);
        sp.setStarted(false);

        List<Task> spTasks = sp.getTasks();

        //each task has not done yet -> to the backlog
        spTasks.forEach(t->{
            if(!t.getStatus().equals(Status.DONE) && !t.getStatus().equals(Status.COMPLETE)){
                t.setSprint(projectService.getSprintById(toSprint));
                projectService.addTask(t);
            }

            if(t.getStatus().equals(Status.DONE)){
                t.setStatus(Status.COMPLETE);
                projectService.addTask(t);
            }
        });

        projectService.addSprint(sp);

        return new ResponseEntity<>("Sprint complete successfully",HttpStatus.OK);

    }

    //Get Project Sprint and Task -> for backlog page
    @GetMapping("/{projectId}/sprints_tasks")
    public ResponseEntity<Object> getProjectSprintAndTask(@PathVariable("projectId") Long projectId){
        //get Sprint without backlog
        List<Sprint> sprints = projectService.getProjectSprints(projectId);
        sprints.remove(projectService.getSprintByNameAndProjectId("Backlog",projectId));

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



    //Get Sprint by project also get tasks :) without backlog sprint
    @GetMapping("/{projectId}/sprints")
    public ResponseEntity<Object> getProjectSprint(@PathVariable("projectId") Long projectId){

        List<Sprint> sprints = projectService.getProjectSprints(projectId);
        sprints.remove(projectService.getSprintByNameAndProjectId("Backlog",projectId));

        return new ResponseEntity<>(sprints,HttpStatus.OK);
    }


    @PostMapping("/{projectId}/sprint/{sprintId}/task/{taskId}")
    @Transactional
    public ResponseEntity<Object> changeTaskSprint(@PathVariable("projectId") Long projectId,@PathVariable("sprintId") Long sprintId,@PathVariable("taskId") Long taskId){

        //if sprint==backlog -> assigneTo null, priority null & status null
        Task task = projectService.getTaskById(taskId);
        if(Objects.equals(task.getSprint().getId(), sprintId))
            throw new DataAlreadyExists("This task is in this sprint");


        Sprint newSp = projectService.getSprintById(sprintId);
        if(newSp.getName().equals("Backlog")){
            task.setStatus(null);
            task.setPriority(null);
            task.setAssigneTo(null);
        }

        task.setSprint(newSp);
        projectService.addTask(task);

        return new ResponseEntity<>(task,HttpStatus.ACCEPTED);
    }



    @Transactional
    @PostMapping("/{projectId}/sprint/{sprintId}/delete")
    public ResponseEntity<Object> deleteSprint(@PathVariable("projectId") Long projectId,@PathVariable("sprintId") Long sprintId){
        Sprint sprint = projectService.getSprintById(sprintId);

        System.out.println(sprint.getName());



        Sprint backlogSprint = projectService.getSprintByNameAndProjectId("Backlog",projectId);

        System.out.println(backlogSprint.getName());

        sprint.getTasks().forEach(t->{
            t.setSprint(backlogSprint);
            t.setStatus(null);
            t.setPriority(null);
            t.setAssigneTo(null);
            backlogSprint.getTasks().add(t);
        });

        projectService.addSprint(backlogSprint);


        projectService.deleteSprint(sprintId);

        return new ResponseEntity<>("Sprint has been deleted successfully",HttpStatus.OK);
    }


    @Transactional
    @PostMapping("/{projectId}/sprint/{sprintId}/update")
    public ResponseEntity<Object> updateSprint(@PathVariable("projectId") Long projectId,@PathVariable("sprintId") Long sprintId,@Valid @RequestBody SprintMapper sprintMapper){

        System.out.println(sprintMapper);
        Sprint sprint = projectService.getSprintByIdAndProjectId(sprintId,projectId);

        LocalDateTime startDate = LocalDateTime.ofInstant(sprintMapper.getStartDate().toInstant(), ZoneId.systemDefault());

        LocalDateTime endDate = LocalDateTime.ofInstant(sprintMapper.getEndDate().toInstant(), ZoneId.systemDefault());

        sprint.setName(sprintMapper.getName());
        sprint.setStartDate(startDate);
        sprint.setEndDate(endDate);

        projectService.addSprint(sprint);

        return new ResponseEntity<>(sprint,HttpStatus.OK);
    }



    @Transactional
    @PostMapping("/{projectId}/sprint/add")
    public ResponseEntity<Object> addSprint(@PathVariable("projectId") Long projectId,@Valid @RequestBody SprintMapper sprintMapper){

        LocalDateTime startDate = LocalDateTime.ofInstant(sprintMapper.getStartDate().toInstant(), ZoneId.systemDefault());
        LocalDateTime endDate = LocalDateTime.ofInstant(sprintMapper.getEndDate().toInstant(), ZoneId.systemDefault());


        Sprint sprint = new Sprint();

        if(!projectService.sprintNameExists(sprintMapper.getName())){
            sprint.setName(sprintMapper.getName());
            sprint.setStartDate(startDate);
            sprint.setEndDate(endDate);
            sprint.setProject(projectService.findProjectById(projectId));
            projectService.addSprint(sprint);
        }

        return new ResponseEntity<>(sprint,HttpStatus.CREATED);

    }



    //Add task to sprint
    @PostMapping("/{projectId}/task/add")
    public ResponseEntity<Object> addTask(@PathVariable("projectId") Long projectId,@Valid @RequestBody TaskNameMapper taskMapper){

        Sprint sp = projectService.getSprintById(taskMapper.getSprintId());
        if(sp==null)
            throw new DataNotFound("Sprint not found");

        Task task = new Task(taskMapper.getName());

        if(projectService.checkSprintProject(projectId,sp.getName())){

            task.setSprint(sp);

            task.setStatus(null);

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
    public ResponseEntity<Object> updateTask(@PathVariable("taskId") Long taskId,@RequestBody TaskMapper ts,@RequestHeader("Authorization") String authorization) throws JsonProcessingException {

        Task task = projectService.getTaskById(taskId);

        if(task==null){
            throw new DataNotFound("Task not found");
        }


        if(!Objects.equals(task.getAssigneTo(), ts.getAssigneTo()) && ts.getAssigneTo()!=null){
            //send message
            UsersDTO user = usersClient.getUserDetails(ts.getAssigneTo(),authorization);
            KafkaReportDTO kafkaReportDTO = new KafkaReportDTO();
            kafkaReportDTO.setProjectId(String.valueOf(task.getSprint().getProject().getId()));
            kafkaReportDTO.setProjectName(task.getSprint().getProject().getName());
            kafkaReportDTO.setEvent("Assigne To");
            kafkaReportDTO.setUserId(String.valueOf(user.getId()));
            kafkaReportDTO.setUserEmail(user.getEmail());
            kafkaReportDTO.setUserAvatar(user.getAvatar());
            kafkaReportDTO.setDescription("Task has been assigne to "+user.getUsername());
            kafkaReportDTO.setPhoneNumber(user.getPhoneNumber());
            projectService.sendAssigne(kafkaReportDTO);
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



    /*
    @PostMapping("/sendHistoryNotification")
    public ResponseEntity<Object> sendHistoryToNotificationService(@RequestBody KafkaReportDTO kafkaReportDTO) throws JsonProcessingException {
        return new ResponseEntity<>(projectService.send(kafkaReportDTO),HttpStatus.OK);
    }
    */



    /*User Api*/
    @PostMapping("/{projectId}/add/people")
    public ResponseEntity<Object> addPeopleToProject(@PathVariable("projectId") Long projectId,@RequestBody Map<String,String> data,@RequestHeader("Authorization") String authorization){
        //Check if email exists in platform
        UsersDTO usersDTO = usersClient.getUserByEmail(data.get("email"),authorization.substring(7));

        System.out.println(usersDTO);
        //If User already exist in this project
        if(userProjectClient.checkIfUserExistProject(projectId,usersDTO.getId()))
            throw new DataAlreadyExists("User already exists in this project");


        //If Exist send email to user

        //TODO send mail to user
        //Add user to project
        userProjectClient.addUserToProject(projectId,usersDTO.getId());

        return new ResponseEntity<>(usersDTO,HttpStatus.CREATED);
    }

}
