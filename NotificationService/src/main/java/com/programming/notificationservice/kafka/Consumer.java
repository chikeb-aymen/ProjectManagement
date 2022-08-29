package com.programming.notificationservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.notificationservice.entities.Report;
import com.programming.notificationservice.repositories.ReportRepository;
import com.programming.notificationservice.services.TwilioService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class Consumer {

    private ReportRepository reportRepository;

    private static final String reportTopic = "${topic.name}";

    private static final String assigneToTopic = "${topic.assigne}";

    private final ObjectMapper objectMapper;

    private TwilioService twilioService;


    @KafkaListener(topics = reportTopic)
    public void consumeMessage(String message) throws JsonProcessingException {
        log.info("Message consumed {}",message);

        Report kafkaReportDTO = objectMapper.readValue(message,Report.class);

        reportRepository.save(kafkaReportDTO);
    }


    @KafkaListener(topics = assigneToTopic)
    public void assigneToMessage(String message) throws JsonProcessingException {
        log.info("Assigne To Message consumed {}",message);

        Report kafkaReportDTO = objectMapper.readValue(message,Report.class);

        twilioService.assigneToMessage(kafkaReportDTO.getPhoneNumber(),kafkaReportDTO.getProjectName());

        reportRepository.save(kafkaReportDTO);

    }
}
