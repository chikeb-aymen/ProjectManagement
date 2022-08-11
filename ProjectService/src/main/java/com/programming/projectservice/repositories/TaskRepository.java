package com.programming.projectservice.repositories;

import com.programming.projectservice.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {

    List<Task> findBySprintNameAndSprintProjectId(String sp, Long projectId);

    List<Task> findBySprintProjectId(Long prId);

}
