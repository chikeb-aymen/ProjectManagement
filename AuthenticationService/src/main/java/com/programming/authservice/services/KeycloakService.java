package com.programming.authservice.services;

import com.programming.authservice.config.KeycloakClientConfig;
import com.programming.authservice.entities.Users;
import com.programming.authservice.exceptions.DataAlreadyExists;
import lombok.AllArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import java.util.List;

@Service
@AllArgsConstructor
public class KeycloakService {

    private Keycloak keycloak;

    private final KeycloakClientConfig keycloakClientConfig;

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


    public ResponseEntity<AccessTokenResponse> login(String username,String password){

        Keycloak keyProvider = keycloakClientConfig.newKeycloakBuilderWithPasswordCredentials(username,password).build();
        AccessTokenResponse accessTokenResponse = null;

        try {

            accessTokenResponse = keyProvider.tokenManager().getAccessToken();
            if(accessTokenResponse!=null){
                Users users = usersService.getUserByUsername(username);
                accessTokenResponse.setOtherClaims("userId",users.getId());
            }

            return ResponseEntity.status(HttpStatus.OK).body(accessTokenResponse);

        }catch (BadRequestException e){

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(accessTokenResponse);

        }

    }


}
