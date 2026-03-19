package com.taskmanager.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskEvent {
    private String eventType;
    private Long taskId;
    private String title;
    private String userEmail;
    private String timestamp;
}
