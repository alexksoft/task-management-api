package com.taskmanager.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.api.dto.AuthResponse;
import com.taskmanager.api.dto.LoginRequest;
import com.taskmanager.api.dto.RegisterRequest;
import com.taskmanager.api.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private AuthResponse testAuthResponse;

    @BeforeEach
    void setUp() {
        testAuthResponse = new AuthResponse("jwt-token", "Test User", "test@example.com");
    }

    @Test
    void register_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(authService.register(any(RegisterRequest.class))).thenReturn(testAuthResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(authService.login(any(LoginRequest.class))).thenReturn(testAuthResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }
}
