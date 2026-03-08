package com.taskmanager.api.dto;

import com.taskmanager.api.entity.Task.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TaskRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    private LocalDate dueDate;
    
    private TaskStatus status;
}
