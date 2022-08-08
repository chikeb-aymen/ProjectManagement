package com.programming.projectservice.feign;

import com.programming.projectservice.dto.UserProjectDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient("USERPROJECT-SERVICE")
public interface UserProjectClient {

    @GetMapping(value = "/api/v1/user-project/project/{projectId}")
    List<UserProjectDTO> getUsersByProject(@PathVariable("projectId") Long id);
}
