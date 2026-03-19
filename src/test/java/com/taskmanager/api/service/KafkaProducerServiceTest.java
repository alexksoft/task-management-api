package com.taskmanager.api.service;

import com.taskmanager.api.dto.TaskEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, TaskEvent> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    private TaskEvent testEvent;

    @BeforeEach
    void setUp() {
        testEvent = new TaskEvent();
        testEvent.setEventType("TASK_CREATED");
        testEvent.setTaskId(1L);
        testEvent.setTitle("Test Task");
        testEvent.setUserEmail("test@example.com");
        testEvent.setTimestamp("2024-01-15T10:30:00");
    }

    @Test
    void sendTaskEvent_Success() {
        kafkaProducerService.sendTaskEvent(testEvent);

        verify(kafkaTemplate).send(eq("task-events"), eq("1"), eq(testEvent));
    }
}
