package com.krasnopolskyi.fitcoach.http.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.krasnopolskyi.fitcoach.dto.request.trainer.TrainerDto;
import com.krasnopolskyi.fitcoach.dto.request.trainer.TrainerUpdateDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainerProfileDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainingResponseDto;
import com.krasnopolskyi.fitcoach.entity.Trainer;
import com.krasnopolskyi.fitcoach.entity.TrainingType;
import com.krasnopolskyi.fitcoach.entity.User;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.GymException;
import com.krasnopolskyi.fitcoach.exception.ValidateException;
import com.krasnopolskyi.fitcoach.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TrainerControllerTest {

    @InjectMocks
    private TrainerController trainerController;

    @Mock
    private TrainerService trainerService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getTrainer_ShouldReturnTrainerProfileDto_WhenExists() throws EntityException {
        // Arrange
        String username = "trainer1";
        TrainerProfileDto trainerProfile = new TrainerProfileDto("John", "Doe", "john.doe", "Cardio", true, new ArrayList<>());

        when(trainerService.findByUsername(username)).thenReturn(trainerProfile);

        // Act
        ResponseEntity<TrainerProfileDto> response = trainerController.getTrainer(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(trainerProfile, response.getBody());
    }

    @Test
    void findTraining_ShouldReturnListOfTrainingResponseDto_WhenExists() throws EntityException {
        // Arrange
        String username = "trainer1";
        LocalDate periodFrom = LocalDate.now().minusDays(7);
        LocalDate periodTo = LocalDate.now();
        String partner = "partner1";

        TrainingResponseDto trainingResponseDto = new TrainingResponseDto(1L, "Strength Training", "Strength", "Trainer FullName", "Trainee FullName", LocalDate.now(), 60);
        List<TrainingResponseDto> trainingResponseDtos = List.of(trainingResponseDto);
        when(trainerService.getTrainings(any())).thenReturn(trainingResponseDtos);

        // Act
        ResponseEntity<List<TrainingResponseDto>> response = trainerController.findTraining(username, periodFrom, periodTo, partner);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(trainingResponseDtos, response.getBody());
    }

    @Test
    void createTrainer_ShouldReturnCreatedUserCredentials() throws EntityException {
        // Arrange
        TrainerDto trainerDto = new TrainerDto();
        trainerDto.setUserId(23L);
        trainerDto.setFirstName("John");
        trainerDto.setLastName("Doe");
        trainerDto.setUsername("john.doe");
        trainerDto.setSpecialization(1);
        trainerDto.setIsActive(true);

        Trainer trainer = new Trainer();

        User user = new User();
        user.setId(23L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("john.doe");
        user.setActive(true);

        trainer.setUser(user);
        trainer.setSpecialization(new TrainingType(1, "Cardio"));

        when(trainerService.save(any())).thenReturn(trainer);

        // Act
        ResponseEntity<Trainer> response = trainerController.createTrainer(trainerDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("john.doe", response.getBody().getUser().getUsername());
    }

    @Test
    void updateTrainer_ShouldReturnUpdatedTrainerProfileDto() throws GymException {
        // Arrange
        TrainerUpdateDto trainerUpdateDto = new TrainerUpdateDto("john.doe","John", "Doe",  "Cardio", true);
        TrainerProfileDto updatedTrainerProfileDto = new TrainerProfileDto("John", "Doe", "john.doe", "Cardio", true, new ArrayList<>());

        when(trainerService.update(anyString(), any())).thenReturn(updatedTrainerProfileDto);

        // Act
        ResponseEntity<TrainerProfileDto> response = trainerController.updateTrainer("john.doe", trainerUpdateDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(updatedTrainerProfileDto, response.getBody());
    }

}
