package com.programming.projectservice.repositories;

import com.programming.projectservice.entities.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprintRepository extends JpaRepository<Sprint,Long> {

    boolean existsByName(String sprintName);

    Sprint findByName(String sp);

    Sprint findByNameAndProjectId(String nm,Long id);

}
