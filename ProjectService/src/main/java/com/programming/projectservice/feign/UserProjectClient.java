package com.programming.projectservice.feign;

import com.programming.projectservice.dto.UserProjectDTO;
import feign.Headers;
import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("USERPROJECT-SERVICE")
public interface UserProjectClient {

    @GetMapping(value = "/api/v1/user-project/project/{projectId}")
    List<UserProjectDTO> getUsersByProject(@PathVariable("projectId") Long id);



    @GetMapping(value = "/api/v1/user-project/user/{userId}/projects")
    List<UserProjectDTO> getUserProjects(@PathVariable("userId") Long id);



    @GetMapping(value = "/api/v1/user-project/user/{userId}/project/{projectId}")
    boolean checkIfUserExistProject(@PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId);


    @PostMapping(value = "/api/v1/user-project/project/{projectId}/add/user/{userId}")
    UserProjectDTO addUserToProject(@PathVariable("projectId") Long projectId,@PathVariable("userId") Long userId);
}
