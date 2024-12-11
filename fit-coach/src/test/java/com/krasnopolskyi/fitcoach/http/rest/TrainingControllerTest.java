package com.krasnopolskyi.fitcoach.http.rest;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.krasnopolskyi.fitcoach.dto.request.TrainingDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainingResponseDto;
import com.krasnopolskyi.fitcoach.exception.AuthnException;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.ValidateException;
import com.krasnopolskyi.fitcoach.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

class TrainingControllerTest {

    @InjectMocks
    private TrainingController trainingController;

    @Mock
    private TrainingService trainingService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(trainingController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void addTraining_ShouldReturnCreatedTraining() throws Exception {
        // Arrange
        TrainingDto trainingDto = new TrainingDto("trainee1", "trainer1", "Strength Training", LocalDate.now(), 60);
        TrainingResponseDto trainingResponseDto = new TrainingResponseDto(1L, "Strength Training", "Strength", "Trainer FullName", "Trainee FullName", LocalDate.now(), 60);

        when(trainingService.save(trainingDto)).thenReturn(trainingResponseDto);

        // Act & Assert (MockMvc way)
        mockMvc.perform(post("/api/v1/fit-coach/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.trainingName", is("Strength Training")))
                .andExpect(jsonPath("$.trainingType", is("Strength")))
                .andExpect(jsonPath("$.trainerFullName", is("Trainer FullName")))
                .andExpect(jsonPath("$.traineeFullName", is("Trainee FullName")))
                .andExpect(jsonPath("$.duration", is(60)));
    }

    @Test
    void addTraining_ShouldReturnCreatedTraining_ResponseEntityWay() throws EntityException, ValidateException, AuthnException {
        // Arrange
        TrainingDto trainingDto = new TrainingDto("trainee1", "trainer1", "Strength Training", LocalDate.now(), 60);
        TrainingResponseDto trainingResponseDto = new TrainingResponseDto(1L, "Strength Training", "Strength", "Trainer FullName", "Trainee FullName", LocalDate.now(), 60);

        when(trainingService.save(trainingDto)).thenReturn(trainingResponseDto);

        // Act
        ResponseEntity<TrainingResponseDto> response = trainingController.addTraining(trainingDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(trainingResponseDto, response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("Strength Training", response.getBody().trainingName());
        assertEquals("Strength", response.getBody().trainingType());
    }

}
