package com.vladimirkomlev.workoutdiary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladimirkomlev.workoutdiary.dto.ConfirmationRequestDto;
import com.vladimirkomlev.workoutdiary.dto.UserRequestDto;
import com.vladimirkomlev.workoutdiary.dto.UserResponseDto;
import com.vladimirkomlev.workoutdiary.model.User;
import com.vladimirkomlev.workoutdiary.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RegistrationControllerUnitTest {
    private MockMvc mockMvc;
    private UserService userService = mock(UserService.class);
    private RegistrationController registrationController = new RegistrationController(userService);
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(registrationController).build();
    }

    @Test
    public void signUp() throws Exception {
        String firstName = "John";
        String lastName = "Doe";
        String email = "test@myemail.com";
        String password = "Password1!";
        int age = 21;
        UserRequestDto request = new UserRequestDto();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        request.setPassword(password);
        request.setAge(age);
        UserResponseDto response = new UserResponseDto();
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setEmail(email);
        response.setAge(age);

        MvcResult mvcResult = mockMvc.perform(post("/signup")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String actualBody = mvcResult.getResponse().getContentAsString();
        String expectedBody = objectMapper.writeValueAsString(response);

        Assert.assertThat(actualBody, equalTo(expectedBody));
        verify(userService, times(1)).register(any(User.class));
    }

    @Test
    public void signUpWithEmailInUpperCase() throws Exception {
        String firstName = "John";
        String lastName = "Doe";
        String email = "TEST@MyEmail.com";
        String password = "Password1!";
        int age = 21;
        UserRequestDto request = new UserRequestDto();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        request.setPassword(password);
        request.setAge(age);
        UserResponseDto response = new UserResponseDto();
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setEmail("test@myemail.com");
        response.setAge(age);

        MvcResult mvcResult = mockMvc.perform(post("/signup")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String actualBody = mvcResult.getResponse().getContentAsString();
        String expectedBody = objectMapper.writeValueAsString(response);

        Assert.assertThat(actualBody, equalTo(expectedBody));
    }

    @Test
    public void signUpWithBlankFirstName() throws Exception {
        String lastName = "Doe";
        String email = "test@myemail.com";
        String password = "Password1!";
        int age = 21;
        UserRequestDto request = new UserRequestDto();
        request.setLastName(lastName);
        request.setEmail(email);
        request.setPassword(password);
        request.setAge(age);

        mockMvc.perform(post("/signup")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void signUpWithBlankLastName() throws Exception {
        String firstName = "John";
        String email = "TEST@MyEmail.com";
        String password = "Password1!";
        int age = 21;
        UserRequestDto request = new UserRequestDto();
        request.setFirstName(firstName);
        request.setEmail(email);
        request.setPassword(password);
        request.setAge(age);

        mockMvc.perform(post("/signup")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void signUpWithBlankEmail() throws Exception {
        String firstName = "John";
        String lastName = "Doe";
        String password = "Password1!";
        int age = 21;
        UserRequestDto request = new UserRequestDto();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setPassword(password);
        request.setAge(age);

        mockMvc.perform(post("/signup")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void signUpWithBlankPassword() throws Exception {
        String firstName = "John";
        String lastName = "Doe";
        String email = "test@myemail.com";
        int age = 21;
        UserRequestDto request = new UserRequestDto();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        request.setAge(age);

        mockMvc.perform(post("/signup")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void signUpWithBlankAge() throws Exception {
        String firstName = "John";
        String lastName = "Doe";
        String email = "test@myemail.com";
        String password = "Password1!";
        UserRequestDto request = new UserRequestDto();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        request.setPassword(password);

        mockMvc.perform(post("/signup")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void signUpWithInvalidEmailFormat() throws Exception {
        String firstName = "John";
        String lastName = "Doe";
        String email = "test";
        String password = "Password1!";
        int age = 21;
        UserRequestDto request = new UserRequestDto();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        request.setPassword(password);
        request.setAge(age);

        mockMvc.perform(post("/signup")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void signUpWithInvalidPasswordFormat() throws Exception {
        String firstName = "John";
        String lastName = "Doe";
        String email = "test@myemail.com";
        String password = "password";
        int age = 21;
        UserRequestDto request = new UserRequestDto();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        request.setPassword(password);
        request.setAge(age);

        mockMvc.perform(post("/signup")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void signUpWithInvalidAgeFormat() throws Exception {
        String firstName = "John";
        String lastName = "Doe";
        String email = "test@myemail.com";
        String password = "Password!1";
        int age = 1000;
        UserRequestDto request = new UserRequestDto();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        request.setPassword(password);
        request.setAge(age);

        mockMvc.perform(post("/signup")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void confirm() throws Exception {
        String firstName = "John";
        String lastName = "Doe";
        User mockUser = new User();
        mockUser.setFirstName(firstName);
        mockUser.setLastName(lastName);
        when(userService.confirm(anyString())).thenReturn(mockUser);
        ConfirmationRequestDto request = new ConfirmationRequestDto();
        request.setCode("code");
        UserResponseDto response = new UserResponseDto();
        response.setFirstName(firstName);
        response.setLastName(lastName);
        MvcResult mvcResult = mockMvc.perform(post("/confirm")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String actualBody = mvcResult.getResponse().getContentAsString();
        String expectedBody = objectMapper.writeValueAsString(response);

        Assert.assertThat(actualBody, equalTo(expectedBody));
    }
}