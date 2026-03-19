package com.taskmanager.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.api.dto.TaskRequest;
import com.taskmanager.api.dto.TaskResponse;
import com.taskmanager.api.entity.Task;
import com.taskmanager.api.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private TaskResponse testTaskResponse;
    private TaskRequest testTaskRequest;

    @BeforeEach
    void setUp() {
        testTaskResponse = new TaskResponse(
                1L,
                "Test Task",
                "Test Description",
                LocalDate.now().plusDays(7),
                Task.TaskStatus.PENDING
        );

        testTaskRequest = new TaskRequest();
        testTaskRequest.setTitle("Test Task");
        testTaskRequest.setDescription("Test Description");
        testTaskRequest.setDueDate(LocalDate.now().plusDays(7));
        testTaskRequest.setStatus(Task.TaskStatus.PENDING);
    }

    @Test
    @WithMockUser
    void createTask_Success() throws Exception {
        when(taskService.createTask(any(TaskRequest.class))).thenReturn(testTaskResponse);

        mockMvc.perform(post("/api/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTaskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @WithMockUser
    void getAllTasks_Success() throws Exception {
        Page<TaskResponse> taskPage = new PageImpl<>(List.of(testTaskResponse));
        when(taskService.getAllTasks(0, 10)).thenReturn(taskPage);

        mockMvc.perform(get("/api/tasks")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Task"));
    }

    @Test
    @WithMockUser
    void getTaskById_Success() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(testTaskResponse);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @WithMockUser
    void updateTask_Success() throws Exception {
        when(taskService.updateTask(eq(1L), any(TaskRequest.class))).thenReturn(testTaskResponse);

        mockMvc.perform(put("/api/tasks/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTaskRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @WithMockUser
    void deleteTask_Success() throws Exception {
        mockMvc.perform(delete("/api/tasks/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
