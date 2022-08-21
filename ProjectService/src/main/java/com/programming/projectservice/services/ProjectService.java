package com.programming.projectservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.programming.projectservice.dto.KafkaReportDTO;
import com.programming.projectservice.entities.Project;
import com.programming.projectservice.entities.Sprint;
import com.programming.projectservice.entities.Task;
import com.programming.projectservice.exceptions.DataAlreadyExists;
import com.programming.projectservice.exceptions.DataNotFound;
import com.programming.projectservice.kafka.Producer;
import com.programming.projectservice.repositories.ProjectRepository;
import com.programming.projectservice.repositories.SprintRepository;
import com.programming.projectservice.repositories.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ProjectService {

    private SprintRepository sprintRepository;
    private ProjectRepository projectRepository;

    private TaskRepository taskRepository;

    private Producer producer;


    public Project findProjectById(Long id){
        if(projectRepository.findById(id).isEmpty())
            throw new DataNotFound("Project with id -"+id+"- not found");

        return projectRepository.findById(id).get();
    }


    public Sprint getSprintById(Long sprintId){
        return sprintRepository.findById(sprintId).get();
    }
    public boolean sprintNameExists(String sprintName){
        if(sprintRepository.existsByName(sprintName)){
            throw new DataAlreadyExists("Sprint with name -"+sprintName+"- already exists");
        }

        return false;
    }

    public boolean checkSprintProject(Long projectId,String sprintName){
        Sprint sprint = sprintRepository.findByName(sprintName);
        if(sprint==null){
            throw new DataNotFound("Sprint not found in this project");
        }
        if(!Objects.equals(sprint.getProject().getId(), projectId)){
            throw new DataNotFound("Sprint not found in this project");
        }

        return true;
    }


    public Sprint getSprintByNameContainAndProjectId(String sp,Long projectId){
        return sprintRepository.findSprintByNameContainsAndProjectId(sp,projectId);
    }


    public Sprint getSprintByNameAndProjectId(String sp,Long projectId){
        return sprintRepository.findByNameAndProjectId(sp,projectId);
    }

    public List<Task> getTasksBySprintAndProject(String sp, Long projectId){
        return taskRepository.findBySprintNameAndSprintProjectId(sp,projectId);
    }

    public List<Task> getTaskByProjectId(Long id){
        return taskRepository.findBySprintProjectId(id);
    }

    public List<Task> getTaskByUserAssigned(Long userId){
        return taskRepository.findByAssigneTo(userId);
    }

    public Task getTaskById(Long id){

        if(taskRepository.findById(id).isEmpty()){
            throw new DataNotFound("Task not found");
        }
        return taskRepository.findById(id).get();
    }


    //get sprint by sprintID and projectID
    public Sprint getSprintByIdAndProjectId(Long sprintId,Long projectId){
        if(sprintRepository.findSprintByIdAndProjectId(sprintId,projectId)==null)
            throw new DataNotFound("Sprint not exists in this project");

        return sprintRepository.findSprintByIdAndProjectId(sprintId,projectId);
    }

    //get all sprint in project
    public List<Sprint> getProjectSprints(Long projectId){
        if(sprintRepository.findByProjectId(projectId)==null)
            throw new DataNotFound("No sprint found in this project, try to add one");

        return sprintRepository.findByProjectId(projectId);
    }


    @Transactional
    public void addSprint(Sprint sp){
        sprintRepository.save(sp);
    }


    public void deleteSprint(Long sprintId){
        sprintRepository.deleteById(sprintId);
    }


    @Transactional
    public void addTask(Task t){
        taskRepository.save(t);
    }






    //Send History to notification service
    public String send(KafkaReportDTO kafkaReportDTO) throws JsonProcessingException {
        return producer.sendMessage(kafkaReportDTO);
    }


}
