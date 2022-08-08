package com.programming.userprojectservice.repositories;

import com.programming.userprojectservice.entities.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProject, Long> {
    List<UserProject> findAllByUserId(Long userId);
    List<UserProject> findAllByProjectId(Long projectId);
}
