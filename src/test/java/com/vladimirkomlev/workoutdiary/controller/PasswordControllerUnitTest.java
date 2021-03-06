package com.vladimirkomlev.workoutdiary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladimirkomlev.workoutdiary.dto.ResetPasswordRequestDto;
import com.vladimirkomlev.workoutdiary.dto.SetupPasswordRequestDto;
import com.vladimirkomlev.workoutdiary.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PasswordControllerUnitTest {
    private MockMvc mockMvc;
    private UserService userService = mock(UserService.class);
    private PasswordController passwordController = new PasswordController(userService);
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(passwordController).build();
    }

    @Test
    public void resetPassword() throws Exception {
        ResetPasswordRequestDto request = new ResetPasswordRequestDto();
        request.setEmail("test@myemail.com");

        mockMvc.perform(post("/reset-password")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        verify(userService, times(1)).resetPassword(any(ResetPasswordRequestDto.class));
    }

    @Test
    public void resetPasswordWithBlankEmail() throws Exception {
        ResetPasswordRequestDto request = new ResetPasswordRequestDto();

        mockMvc.perform(post("/reset-password")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void setupPassword() throws Exception {
        SetupPasswordRequestDto request = new SetupPasswordRequestDto();
        request.setPassword("Password!1");
        request.setCode("code");

        mockMvc.perform(post("/setup-password")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        verify(userService, times(1)).setupPassword(any(SetupPasswordRequestDto.class));
    }

    @Test
    public void setupPasswordWithBlankPassword() throws Exception {
        SetupPasswordRequestDto request = new SetupPasswordRequestDto();
        request.setCode("code");

        mockMvc.perform(post("/setup-password")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void setupPasswordWithBlankCode() throws Exception {
        SetupPasswordRequestDto request = new SetupPasswordRequestDto();
        request.setPassword("Password!1");

        mockMvc.perform(post("/setup-password")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}