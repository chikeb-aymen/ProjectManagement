package com.programming.projectservice.feign;

import com.programming.projectservice.dto.UsersDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("AUTHENTICATION-SERVICE")
public interface UsersClient {

    @GetMapping(value = "/api/v1/users/{userId}")
    UsersDTO getUserDetails(@PathVariable("userId") Long userId);
}
