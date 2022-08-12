package com.programming.notificationservice.controllers;

import com.programming.notificationservice.entities.Report;
import com.programming.notificationservice.repositories.ReportRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/history")
@AllArgsConstructor
public class HistoryNotification {

    private ReportRepository reportRepository;


    @GetMapping("/{projectId}")
    public List<Report> getAllHistoryByProjectId(@PathVariable("projectId") String projectId){
        return reportRepository.findAllByProjectId(projectId);
    }


}
