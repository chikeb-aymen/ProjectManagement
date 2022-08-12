package com.programming.projectservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.projectservice.dto.KafkaReportDTO;
import com.programming.projectservice.entities.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class Producer {

    @Value("${topic.name}")
    private String reportTopic;

    private final ObjectMapper objectMapper;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public Producer(ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public String sendMessage(KafkaReportDTO report) throws JsonProcessingException {
        String objectAsString = objectMapper.writeValueAsString(report);

        kafkaTemplate.send(reportTopic,objectAsString);

        log.info("Task produced {}", objectAsString);

        return "Message sent Successfully";
    }
}
