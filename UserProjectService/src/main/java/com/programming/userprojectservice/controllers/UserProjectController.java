package com.programming.userprojectservice.controllers;

import com.programming.userprojectservice.entities.UserProject;
import com.programming.userprojectservice.services.UserProjectService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
