package com.taskmanager.api.service;

import com.taskmanager.api.dto.TaskEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {
    
    @KafkaListener(topics = "task-events", groupId = "task-management-group")
    public void consumeTaskEvent(TaskEvent event) {
        log.info("Consumed task event: {} - Task ID: {}, Title: {}, User: {}", 
                event.getEventType(), event.getTaskId(), event.getTitle(), event.getUserEmail());
    }
}
