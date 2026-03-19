package com.taskmanager.api.service;

import com.taskmanager.api.dto.TaskRequest;
import com.taskmanager.api.dto.TaskResponse;
import com.taskmanager.api.entity.Task;
import com.taskmanager.api.entity.User;
import com.taskmanager.api.exception.ResourceNotFoundException;
import com.taskmanager.api.repository.TaskRepository;
import com.taskmanager.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setDueDate(LocalDate.now().plusDays(7));
        testTask.setStatus(Task.TaskStatus.PENDING);
        testTask.setUser(testUser);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
    }

    @Test
    void createTask_Success() {
        TaskRequest request = new TaskRequest();
        request.setTitle("New Task");
        request.setDescription("New Description");
        request.setDueDate(LocalDate.now().plusDays(5));
        request.setStatus(Task.TaskStatus.PENDING);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponse response = taskService.createTask(request);

        assertNotNull(response);
        assertEquals("Test Task", response.getTitle());
        verify(taskRepository).save(any(Task.class));
        verify(kafkaProducerService).sendTaskEvent(any());
    }

    @Test
    void createTask_UserNotFound() {
        TaskRequest request = new TaskRequest();
        request.setTitle("New Task");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(request));
    }

    @Test
    void getAllTasks_Success() {
        Page<Task> taskPage = new PageImpl<>(List.of(testTask));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findByUserId(1L, PageRequest.of(0, 10))).thenReturn(taskPage);

        Page<TaskResponse> result = taskService.getAllTasks(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Task", result.getContent().get(0).getTitle());
    }

    @Test
    void getTaskById_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTask));

        TaskResponse response = taskService.getTaskById(1L);

        assertNotNull(response);
        assertEquals("Test Task", response.getTitle());
    }

    @Test
    void getTaskById_NotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(1L));
    }

    @Test
    void updateTask_Success() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Updated Task");
        request.setDescription("Updated Description");
        request.setDueDate(LocalDate.now().plusDays(10));
        request.setStatus(Task.TaskStatus.COMPLETED);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponse response = taskService.updateTask(1L, request);

        assertNotNull(response);
        verify(taskRepository).save(any(Task.class));
        verify(kafkaProducerService).sendTaskEvent(any());
    }

    @Test
    void deleteTask_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTask));

        taskService.deleteTask(1L);

        verify(taskRepository).delete(testTask);
        verify(kafkaProducerService).sendTaskEvent(any());
    }

    @Test
    void deleteTask_NotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(1L));
    }
}
