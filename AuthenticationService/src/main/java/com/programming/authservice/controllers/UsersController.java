package com.programming.authservice.controllers;

import com.programming.authservice.dtos.UserDTO;
import com.programming.authservice.entities.Users;
import com.programming.authservice.mappers.UserMapper;
import com.programming.authservice.services.UsersService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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


    /**
     * Get User detail for user-project service
     * @param userId
     * @return
     */
    @GetMapping("/details/{userId}")
    public UserDTO getUserDetail(@PathVariable("userId") Long userId){
        System.out.println("----USER ID-----"+userId);
        Users user = usersService.getUserDetail(userId);

        System.out.println("----USER DTO-----");
        System.out.println(UserMapper.UsersToUserDTO(user));

        return UserMapper.UsersToUserDTO(user);
    }



}
