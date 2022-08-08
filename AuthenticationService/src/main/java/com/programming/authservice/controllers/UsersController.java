package com.programming.authservice.controllers;

import com.programming.authservice.entities.Users;
import com.programming.authservice.services.UsersService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UsersController {

    private UsersService usersService;


    @PostMapping("/register")
    public String register(@Valid @RequestBody Users users){
        return "Register";
    }


    @GetMapping("/{id}")
    public Users userDetails(@PathVariable("id") Long userId){
        return usersService.getUserDetail(userId);
    }

}
