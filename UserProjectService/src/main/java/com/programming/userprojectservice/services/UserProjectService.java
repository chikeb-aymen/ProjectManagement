package com.programming.userprojectservice.services;


import com.programming.userprojectservice.entities.UserProject;
import com.programming.userprojectservice.exceptions.DataNotFoundException;
import com.programming.userprojectservice.repositories.UserProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@AllArgsConstructor
public class UserProjectService {

    private UserProjectRepository userProjectRepository;

    public List<UserProject> findUsersByProjectId(Long projectId){
        if(userProjectRepository.findAllByProjectId(projectId).size()<=0){
            throw new DataNotFoundException("No Users found in the project with id "+projectId);
        }
        return userProjectRepository.findAllByProjectId(projectId);
    }

    public List<UserProject> findProjectsByUserId(Long userId){
        if(userProjectRepository.findAllByUserId(userId).size()<=0){
            throw new DataNotFoundException("This user -"+userId+"- is not affected to any project");
        }
        return userProjectRepository.findAllByUserId(userId);
    }
}
