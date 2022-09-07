package com.programming.projectservice.feign;

import com.programming.projectservice.dto.UsersDTO;
import feign.Headers;
import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;


@FeignClient("AUTHENTICATION-SERVICE")
public interface UsersClient {

    //@Headers("Authorization: Bearer {authorization}")
    @GetMapping(value = "/api/v1/users/{userId}")
    UsersDTO getUserDetails(@PathVariable("userId") Long userId,@RequestHeader("Authorization") String authorization);

    //@Headers("Authorization: Bearer {authorization}")
    @GetMapping(value ="/api/v1/users/email/details/{email}", consumes = "application/json", produces = "application/json")
    UsersDTO getUserByEmail(@PathVariable("email") String email, @RequestHeader("Authorization") String authorization);
}
