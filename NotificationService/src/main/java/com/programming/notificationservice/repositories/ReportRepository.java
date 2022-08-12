package com.programming.notificationservice.repositories;

import com.programming.notificationservice.entities.Report;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends MongoRepository<Report,String> {
}
