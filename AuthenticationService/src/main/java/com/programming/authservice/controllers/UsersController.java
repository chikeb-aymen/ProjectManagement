package com.programming.authservice.controllers;

import com.programming.authservice.dtos.KeycloakUserRequest;
import com.programming.authservice.dtos.UserDTO;
import com.programming.authservice.entities.Users;
import com.programming.authservice.exceptions.DataAlreadyExists;
import com.programming.authservice.mappers.UserMapper;
import com.programming.authservice.services.KeycloakService;
import com.programming.authservice.services.UsersService;
import lombok.AllArgsConstructor;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UsersController {

    private UsersService usersService;


    private KeycloakService keycloakService;

    @PostMapping("/auth/register")
    public ResponseEntity<Object> register(@RequestBody Users userRequest){
        System.out.println("REGISTER");
        Response response = keycloakService.addKeycloakUser(userRequest);
        String userId = CreatedResponseUtil.getCreatedId(response);
        //assigne user role

        keycloakService.assigneRoleToUser(userId,keycloakService.findRoleByName("USER"));

        if(response.getStatus() != 201)
            throw new DataAlreadyExists("User was not created");

        return new ResponseEntity<>(userRequest, HttpStatus.CREATED);

    }

    @PostMapping("/auth/login")
    public ResponseEntity<AccessTokenResponse> login(@RequestBody KeycloakUserRequest userRequest){
        return keycloakService.login(userRequest.getUsername(),userRequest.getPassword());
    }


    @GetMapping("/{id}")
    public Users userDetails(@PathVariable("id") Long userId,@RequestHeader("Authorization") String authorization){
        System.out.println("ENTER TO USER DETAILS /ID");
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



    @GetMapping("/email/details/{email}")
    public UserDTO getUserDetail(@PathVariable("email") String data){

        System.out.println("Enter to getUserDetail");
        System.out.println(data);
        Users user = usersService.getUserByEmail(data);

        System.out.println(user);

        //System.out.println(UserMapper.UsersToUserDTO(user));

        return UserMapper.UsersToUserDTO(user);
    }



}
