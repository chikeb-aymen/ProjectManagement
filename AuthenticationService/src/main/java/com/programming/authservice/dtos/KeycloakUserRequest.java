package com.programming.authservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * For login with keycloak
 */
@Data
@AllArgsConstructor @NoArgsConstructor
public class KeycloakUserRequest {

    private String username;

    private String password;

}
