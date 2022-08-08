package com.programming.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor @NoArgsConstructor
public class UsersDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private Boolean isEmailConfirm;


    private String password;


    private String avatar;

    private boolean isAvatarSet;



}
