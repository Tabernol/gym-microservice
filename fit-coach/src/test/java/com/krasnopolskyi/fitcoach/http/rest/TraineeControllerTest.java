package com.krasnopolskyi.fitcoach.http.rest;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.krasnopolskyi.fitcoach.dto.request.trainee.TraineeUpdateDto;
import com.krasnopolskyi.fitcoach.dto.request.training.TrainingFilterDto;
import com.krasnopolskyi.fitcoach.dto.response.TraineeProfileDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainerProfileShortDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainingResponseDto;
import com.krasnopolskyi.fitcoach.dto.response.UserProfileDto;
import com.krasnopolskyi.fitcoach.service.TraineeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

class TraineeControllerTest {

    @InjectMocks
    private TraineeController traineeController;

    @Mock
    private TraineeService traineeService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(traineeController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getTrainee_ShouldReturnTraineeProfile() throws Exception {
        // Arrange
        String username = "john.doe";
        TraineeProfileDto traineeProfile = TraineeProfileDto.builder()
                .username("john.doe")
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .isActive(true)
                .trainers(null)
                .build();

        when(traineeService.findByUsername(username)).thenReturn(traineeProfile);

        // Act & Assert
        mockMvc.perform(get("/api/v1/fit-coach/trainees/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("john.doe")))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")));
    }

    @Test
    void getAllActiveTrainersForTrainee_ShouldReturnListOfTrainers() throws Exception {
        // Arrange
        String username = "john.doe";
        List<TrainerProfileShortDto> trainers = Arrays.asList(
                new TrainerProfileShortDto(new UserProfileDto("trainer1", "Trainer", "One"), "Specialization1"),
                new TrainerProfileShortDto(new UserProfileDto("trainer2", "Trainer", "Two"), "Specialization2")
        );

        when(traineeService.findAllNotAssignedTrainersByTrainee(username)).thenReturn(trainers);

        // Act & Assert
        mockMvc.perform(get("/api/v1/fit-coach/trainees/{username}/trainers/not-assigned", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userProfileDto.username", is("trainer1")))
                .andExpect(jsonPath("$[0].specialization", is("Specialization1")))
                .andExpect(jsonPath("$[1].userProfileDto.username", is("trainer2")))
                .andExpect(jsonPath("$[1].specialization", is("Specialization2")));
    }

    @Test
    void findTraining_ShouldReturnListOfTrainings() throws Exception {
        // Arrange
        String username = "john.doe";
        TrainingFilterDto filter = TrainingFilterDto.builder()
                .owner(username)
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .build();

        List<TrainingResponseDto> trainings = Arrays.asList(
                new TrainingResponseDto(1L, "Strength Training", "Strength", "Trainer One", "Trainee One", LocalDate.now(), 60)
        );

        when(traineeService.getTrainings(filter)).thenReturn(trainings);

        // Act & Assert
        mockMvc.perform(get("/api/v1/fit-coach/trainees/{username}/trainings", username)
                        .param("periodFrom", "2023-01-01")
                        .param("periodTo", "2023-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName", is("Strength Training")))
                .andExpect(jsonPath("$[0].trainingType", is("Strength")));
    }

    @Test
    void updateTrainee_ShouldReturnUpdatedProfile() throws Exception {
        // Arrange
        TraineeUpdateDto traineeUpdateDto = new TraineeUpdateDto("john.doe", "John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St", true);
        TraineeProfileDto updatedProfile = TraineeProfileDto.builder()
                .username("john.doe")
                .firstName("John2")
                .lastName("Doe2")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("12345 Main St")
                .isActive(false)
                .trainers(null)
                .build();

        when(traineeService.update("john.doe", traineeUpdateDto)).thenReturn(updatedProfile);

        // Act & Assert
        mockMvc.perform(put("/api/v1/fit-coach/trainees/{username}", traineeUpdateDto.username())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("john.doe")))
                .andExpect(jsonPath("$.firstName", is("John2")))
                .andExpect(jsonPath("$.lastName", is("Doe2")))
                .andExpect(jsonPath("$.address", is("12345 Main St")))
                .andExpect(jsonPath("$.active", is(false)));
    }

    @Test
    void updateTrainers_ShouldReturnUpdatedTrainerList() throws Exception {
        // Arrange
        String username = "john.doe";
        List<String> trainerUsernames = Arrays.asList("trainer1", "trainer2");
        List<TrainerProfileShortDto> updatedTrainers = Arrays.asList(
                new TrainerProfileShortDto(new UserProfileDto("trainer1", "Trainer", "One"), "Specialization1"),
                new TrainerProfileShortDto(new UserProfileDto("trainer2", "Trainer", "Two"), "Specialization2")
        );

        when(traineeService.updateTrainers(username, trainerUsernames)).thenReturn(updatedTrainers);

        // Act & Assert
        mockMvc.perform(put("/api/v1/fit-coach/trainees/{username}/trainers/update", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerUsernames)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userProfileDto.username", is("trainer1")))
                .andExpect(jsonPath("$[1].userProfileDto.username", is("trainer2")));
    }

    @Test
    void deleteTrainee_ShouldReturnNoContent() throws Exception {
        // Arrange
        String username = "john.doe";
        when(traineeService.delete(username)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/fit-coach/trainees/{username}", username))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTrainee_ShouldReturnNotFound() throws Exception {
        // Arrange
        String username = "john.doe";
        when(traineeService.delete(username)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/fit-coach/trainees/{username}", username))
                .andExpect(status().isNotFound());
    }

}
