package com.programming.projectservice.feign;

import com.programming.projectservice.dto.UsersDTO;
import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;


@FeignClient("AUTHENTICATION-SERVICE")
public interface UsersClient {

    @GetMapping(value = "/api/v1/users/{userId}")
    UsersDTO getUserDetails(@PathVariable("userId") Long userId);

    @GetMapping(value ="/api/v1/users/email/details/{email}")
    UsersDTO getUserByEmail(@PathVariable("email") String email);
}
