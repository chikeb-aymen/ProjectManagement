package com.programming.projectservice.repositories;

import com.programming.projectservice.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project,Long> {

    List<Project> findAllByLeadUser(Long userId);

    boolean existsByLeadUserAndName(Long userId,String name);
}
