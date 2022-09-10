package com.programming.projectservice.repositories;

import com.programming.projectservice.entities.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintRepository extends JpaRepository<Sprint,Long> {

    boolean existsByName(String sprintName);

    boolean existsByNameAndProjectId(String sp,Long projectId);

    Sprint findByNameAndProjectId(String nm,Long id);


    Sprint findSprintByNameContainsAndProjectId(String nm,Long id);

    List<Sprint> findByProjectId(Long projectId);

    void deleteById(Long id);

    Sprint findSprintByIdAndProjectId(Long sprintId,Long prId);

    boolean existsByIsStartedTrueAndProjectId(Long projectId);

    Sprint findByIsStartedTrueAndProjectId(Long projectId);

}
