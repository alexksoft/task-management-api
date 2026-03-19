package com.taskmanager.api.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    
    @Bean
    public NewTopic taskEventsTopic() {
        return TopicBuilder.name("task-events")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
