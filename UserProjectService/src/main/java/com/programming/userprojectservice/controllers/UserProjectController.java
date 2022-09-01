package com.programming.userprojectservice.controllers;

import com.programming.userprojectservice.entities.UserProject;
import com.programming.userprojectservice.services.UserProjectService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-project")
@AllArgsConstructor
public class UserProjectController {


    private UserProjectService userProjectService;

    @GetMapping("/project/{projectId}")
    public List<UserProject> getUsersByProject(@PathVariable("projectId") Long id){
        return userProjectService.findUsersByProjectId(id);
    }

    @GetMapping("/user/{userId}/projects")
    public List<UserProject> getProjectsByUser(@PathVariable("userId") Long id){
        return userProjectService.findProjectsByUserId(id);
    }

    @GetMapping("/user/{userId}/project/{projectId}")
    public boolean checkIfUserExistProject(@PathVariable("projectId") Long projectId, @PathVariable("userId") Long userid){
        return userProjectService.findUserExistProject(projectId,userid);
    }


    @PostMapping("/project/{projectId}/add/user/{userId}")
    public UserProject addUserToProject(@PathVariable("projectId") Long projectId,@PathVariable("userId") Long userid){
        return  userProjectService.addUserToProject(projectId,userid);
    }
}
