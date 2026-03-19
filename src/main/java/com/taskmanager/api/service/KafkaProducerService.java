package com.taskmanager.api.service;

import com.taskmanager.api.dto.TaskEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    
    private final KafkaTemplate<String, TaskEvent> kafkaTemplate;
    private static final String TOPIC = "task-events";
    
    public void sendTaskEvent(TaskEvent event) {
        log.info("Publishing task event: {}", event);
        kafkaTemplate.send(TOPIC, event.getTaskId().toString(), event);
    }
}
