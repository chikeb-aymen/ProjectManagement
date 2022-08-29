package com.programming.notificationservice.controllers;

import com.programming.notificationservice.entities.Report;
import com.programming.notificationservice.repositories.ReportRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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


    @GetMapping("/user/{userId}")
    public List<Report> getUserNotifications(@PathVariable("userId") Long userId){
        return reportRepository.findAllByUserId(String.valueOf(userId));
    }

}
