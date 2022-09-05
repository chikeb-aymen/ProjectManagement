package com.programming.authservice.services;

import com.programming.authservice.dtos.KeycloakUserRequest;
import com.programming.authservice.entities.Users;
import com.programming.authservice.exceptions.DataAlreadyExists;
import lombok.AllArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class KeycloakService {

    private Keycloak keycloak;

    private UsersService usersService;


    public Response addKeycloakUser(Users userRequest){
        CredentialRepresentation password = preparePasswordRepresentation(userRequest.getPassword());
        UserRepresentation userRepresentation = prepareUserRepresentation(userRequest, password);


        try{
            usersService.addUser(userRequest);
        }catch (Exception e){
            throw new DataAlreadyExists("User already exists");
        }

        return keycloak.realm("project-management")
                .users()
                .create(userRepresentation);
    }

    public CredentialRepresentation preparePasswordRepresentation(String password){
        CredentialRepresentation cR = new CredentialRepresentation();
        cR.setTemporary(false);
        cR.setType(CredentialRepresentation.PASSWORD);
        cR.setValue(password);
        return cR;
    }
    
    public UserRepresentation prepareUserRepresentation(Users userRequest,CredentialRepresentation cR){
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(userRequest.getUsername());
        userRepresentation.setCredentials(List.of(cR));
        userRepresentation.setEnabled(true);
        userRepresentation.setEmail(userRequest.getEmail());

        if(userRequest.getFirstName()!=null && userRequest.getLastName()!=null){
            userRepresentation.setFirstName(userRequest.getFirstName());
            userRepresentation.setLastName(userRequest.getLastName());
        }

        return userRepresentation;
    }


    public RoleRepresentation findRoleByName(String role){
        return keycloak.realm("project-management").roles().get(role).toRepresentation();
    }

    public void assigneRoleToUser(String userId,RoleRepresentation roleRepresentation){
        keycloak.realm("project-management")
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(List.of(roleRepresentation));
    }


}
