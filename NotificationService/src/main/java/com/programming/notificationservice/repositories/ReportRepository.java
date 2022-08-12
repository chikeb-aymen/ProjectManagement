package com.programming.notificationservice.repositories;

import com.programming.notificationservice.entities.Report;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends MongoRepository<Report,String> {
    List<Report> findAllByProjectId(String prId);
}
