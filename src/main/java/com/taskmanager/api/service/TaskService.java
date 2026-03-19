package com.taskmanager.api.service;

import com.taskmanager.api.dto.TaskRequest;
import com.taskmanager.api.dto.TaskResponse;
import com.taskmanager.api.entity.Task;
import com.taskmanager.api.entity.User;
import com.taskmanager.api.exception.ResourceNotFoundException;
import com.taskmanager.api.repository.TaskRepository;
import com.taskmanager.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;
    
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
    public TaskResponse createTask(TaskRequest request) {
        User user = getCurrentUser();
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setStatus(request.getStatus() != null ? request.getStatus() : Task.TaskStatus.PENDING);
        task.setUser(user);
        
        task = taskRepository.save(task);
        publishTaskEvent("TASK_CREATED", task, user);
        return toResponse(task);
    }
    
    public Page<TaskResponse> getAllTasks(int page, int size) {
        User user = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        return taskRepository.findByUserId(user.getId(), pageable).map(this::toResponse);
    }
    
    public TaskResponse getTaskById(Long id) {
        User user = getCurrentUser();
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return toResponse(task);
    }
    
    public TaskResponse updateTask(Long id, TaskRequest request) {
        User user = getCurrentUser();
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        
        task = taskRepository.save(task);
        publishTaskEvent("TASK_UPDATED", task, user);
        return toResponse(task);
    }
    
    public void deleteTask(Long id) {
        User user = getCurrentUser();
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        taskRepository.delete(task);
        publishTaskEvent("TASK_DELETED", task, user);
    }
    
    private TaskResponse toResponse(Task task) {
        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), 
                task.getDueDate(), task.getStatus());
    }
    
    private void publishTaskEvent(String eventType, Task task, User user) {
        try {
            com.taskmanager.api.dto.TaskEvent event = new com.taskmanager.api.dto.TaskEvent(
                eventType, task.getId(), task.getTitle(), user.getEmail(), 
                java.time.LocalDateTime.now().toString()
            );
            kafkaProducerService.sendTaskEvent(event);
        } catch (Exception e) {
            log.error("Failed to publish task event: {}", e.getMessage());
        }
    }
}
