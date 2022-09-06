package com.programming.projectservice.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * I add this file to tell : keycloak configuration file is not keycloak.json
 * to force keycloak to depend on spring boot configuration for not search keycloak.json
 */

@Configuration
public class KeycloakAdapterConfig {

    @Bean
    public KeycloakSpringBootConfigResolver keycloakSpringBootConfigResolver(){
        return new KeycloakSpringBootConfigResolver();
    }

}
