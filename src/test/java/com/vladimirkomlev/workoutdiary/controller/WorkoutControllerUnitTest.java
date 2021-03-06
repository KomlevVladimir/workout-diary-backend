package com.vladimirkomlev.workoutdiary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vladimirkomlev.workoutdiary.dto.WorkoutCreateUpdateRequestDto;
import com.vladimirkomlev.workoutdiary.dto.WorkoutResponseDto;
import com.vladimirkomlev.workoutdiary.model.User;
import com.vladimirkomlev.workoutdiary.model.Workout;
import com.vladimirkomlev.workoutdiary.service.WorkoutService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WorkoutControllerUnitTest {
    private MockMvc mockMvc;
    private WorkoutService workoutService = mock(WorkoutService.class);
    private WorkoutController workoutController = new WorkoutController(workoutService);
    private static ObjectMapper objectMapper = new ObjectMapper();

    @BeforeClass
    public static void configureObjectMapper() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(workoutController).build();
    }

    @Test
    public void createWorkout() throws Exception {
        long workoutId = 44;
        String title = "Morning running";
        LocalDate date = LocalDate.parse("2014-05-22");
        String description = "Running 5 miles";
        long currentUserId = 200;
        User mockCurrentUser = new User();
        mockCurrentUser.setId(currentUserId);
        Workout mockWorkout = new Workout(workoutId, date, title, description, mockCurrentUser);
        WorkoutCreateUpdateRequestDto request = new WorkoutCreateUpdateRequestDto(date, description, title);
        when(workoutService.create(any(WorkoutCreateUpdateRequestDto.class), anyLong())).thenReturn(mockWorkout);
        WorkoutResponseDto response = new WorkoutResponseDto();
        response.setId(workoutId);
        response.setTitle(title);
        response.setDate(date);
        response.setDescription(description);

        MvcResult mvcResult = mockMvc.perform(post("/users/{userId}/workouts", currentUserId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String actualBody = mvcResult.getResponse().getContentAsString();
        String expectedBody = objectMapper.writeValueAsString(response);

        Assert.assertThat(actualBody, equalTo(expectedBody));
    }

    @Test
    public void createWorkoutWithBlankDescription() throws Exception {
        WorkoutCreateUpdateRequestDto request = new WorkoutCreateUpdateRequestDto();
        request.setDate(LocalDate.parse("2014-05-22"));
        request.setTitle("Morning running");

        mockMvc.perform(post("/users/{userId}/workouts", 200L)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void createWorkoutWithBlankTitle() throws Exception {
        WorkoutCreateUpdateRequestDto request = new WorkoutCreateUpdateRequestDto();
        request.setDate(LocalDate.parse("2014-05-22"));
        request.setDescription("Swimming 1 mile");

        mockMvc.perform(post("/users/{userId}/workouts", 200L)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void createWorkoutWithBlankDate() throws Exception {
        WorkoutCreateUpdateRequestDto request = new WorkoutCreateUpdateRequestDto();
        request.setDescription("Swimming 1 mile");
        request.setTitle("Swimming");

        mockMvc.perform(post("/users/{userId}/workouts", 200)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void getAllWorkouts() throws Exception {
        long workoutId = 44;
        String title = "Morning running";
        LocalDate date = LocalDate.parse("2014-05-22");
        String description = "Running 5 miles";
        long currentUserId = 200;
        User mockCurrentUser = new User();
        mockCurrentUser.setId(currentUserId);
        Workout mockWorkout = new Workout(workoutId, date, title, description, mockCurrentUser);
        List<Workout> mockWorkouts = new ArrayList<>();
        mockWorkouts.add(mockWorkout);
        when(workoutService.getAllWorkoutsByUserId(currentUserId)).thenReturn(mockWorkouts);
        WorkoutResponseDto expectedWorkoutResponse = new WorkoutResponseDto();
        expectedWorkoutResponse.setId(workoutId);
        expectedWorkoutResponse.setTitle(title);
        expectedWorkoutResponse.setDate(date);
        expectedWorkoutResponse.setDescription(description);

        MvcResult mvcResult = mockMvc.perform(get("/users/{userId}/workouts", currentUserId))
                .andExpect(status().isOk())
                .andReturn();

        String actualBody = mvcResult.getResponse().getContentAsString();
        String expectedBody = objectMapper.writeValueAsString(new ArrayList<>(Collections.singleton(expectedWorkoutResponse)));

        Assert.assertThat(actualBody, equalTo(expectedBody));
    }

    @Test
    public void getWorkout() throws Exception {
        long workoutId = 44;
        String title = "Morning running";
        LocalDate date = LocalDate.parse("2014-05-22");
        String description = "Running 5 miles";
        long currentUserId = 200;
        User mockCurrentUser = new User();
        mockCurrentUser.setId(currentUserId);
        Workout mockWorkout = new Workout(workoutId, date, title, description, mockCurrentUser);
        when(workoutService.get(currentUserId, workoutId)).thenReturn(mockWorkout);
        WorkoutResponseDto expectedWorkoutResponse = new WorkoutResponseDto();
        expectedWorkoutResponse.setId(workoutId);
        expectedWorkoutResponse.setTitle(title);
        expectedWorkoutResponse.setDate(date);
        expectedWorkoutResponse.setDescription(description);

        MvcResult mvcResult = mockMvc.perform(get("/users/{userId}/workouts/{workoutId}", currentUserId, workoutId))
                .andExpect(status().isOk())
                .andReturn();

        String actualBody = mvcResult.getResponse().getContentAsString();
        String expectedBody = objectMapper.writeValueAsString(expectedWorkoutResponse);

        Assert.assertThat(actualBody, equalTo(expectedBody));
    }

    @Test
    public void updateWorkout() throws Exception {
        long workoutId = 44;
        String title = "Morning running";
        LocalDate date = LocalDate.parse("2014-05-22");
        String description = "Running 5 miles";
        long currentUserId = 200;
        User mockCurrentUser = new User();
        mockCurrentUser.setId(currentUserId);
        Workout mockWorkout = new Workout(workoutId, date, title, description, mockCurrentUser);
        WorkoutCreateUpdateRequestDto request = new WorkoutCreateUpdateRequestDto(date, description, title);
        when(workoutService.update(any(WorkoutCreateUpdateRequestDto.class), anyLong(), anyLong())).thenReturn(mockWorkout);
        WorkoutResponseDto expectedWorkoutResponse = new WorkoutResponseDto();
        expectedWorkoutResponse.setId(workoutId);
        expectedWorkoutResponse.setTitle(title);
        expectedWorkoutResponse.setDate(date);
        expectedWorkoutResponse.setDescription(description);

        MvcResult mvcResult = mockMvc.perform(put("/users/{userId}/workouts/{workoutId}", currentUserId, workoutId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String actualBody = mvcResult.getResponse().getContentAsString();
        String expectedBody = objectMapper.writeValueAsString(expectedWorkoutResponse);

        Assert.assertThat(actualBody, equalTo(expectedBody));
    }

    @Test
    public void deleteWorkout() throws Exception {
        long currentUserId = 22;
        long workoutId = 345;
        mockMvc.perform(delete("/users/{userId}/workouts/{workoutId}", currentUserId, workoutId))
                .andExpect(status().isOk())
                .andReturn();

        verify(workoutService, times(1)).delete(currentUserId, workoutId);
    }
}