package com.programming.userprojectservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class UserProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserProjectApplication.class);
    }

}
