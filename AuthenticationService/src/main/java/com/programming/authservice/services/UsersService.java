package com.programming.authservice.services;

import com.programming.authservice.entities.Users;
import com.programming.authservice.exceptions.DataNotFound;
import com.programming.authservice.repositories.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UsersService {

    private UsersRepository usersRepository;


    public Users getUserDetail(Long id){

        if(usersRepository.findById(id).isEmpty()){
            throw new DataNotFound("User with id -"+id+"- not found");
        }

        return usersRepository.findById(id).get();

    }

    public Users getUserByEmail(String email){
        if(usersRepository.findUsersByEmail(email)==null)
            throw new DataNotFound("User with email -"+email+"- not found");

        return usersRepository.findUsersByEmail(email);
    }

}
