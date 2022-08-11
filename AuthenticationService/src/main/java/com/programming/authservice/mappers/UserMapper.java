package com.programming.authservice.mappers;

import com.programming.authservice.dtos.UserDTO;
import com.programming.authservice.entities.Users;

public class UserMapper {

    public static UserDTO UsersToUserDTO(Users user){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setAvatar(user.getAvatar());

        return userDTO;
    }
}
