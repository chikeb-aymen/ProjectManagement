package com.programming.notificationservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.notificationservice.entities.Report;
import com.programming.notificationservice.repositories.ReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Consumer {

    private ReportRepository reportRepository;

    private static final String reportTopic = "${topic.name}";

    private final ObjectMapper objectMapper;

    public Consumer(ReportRepository reportRepository, ObjectMapper objectMapper) {
        this.reportRepository = reportRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = reportTopic)
    public void consumeMessage(String message) throws JsonProcessingException {
        log.info("Message consumed {}",message);

        Report kafkaReportDTO = objectMapper.readValue(message,Report.class);

        reportRepository.save(kafkaReportDTO);


    }
}
