package com.programming.projectservice.repositories;

import com.programming.projectservice.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project,Long> {
}
