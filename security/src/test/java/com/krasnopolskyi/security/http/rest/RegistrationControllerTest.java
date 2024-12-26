package com.krasnopolskyi.security.http.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.krasnopolskyi.security.dto.TraineeDto;
import com.krasnopolskyi.security.dto.TrainerDto;
import com.krasnopolskyi.security.dto.UserCredentials;
import com.krasnopolskyi.security.exception.AuthnException;
import com.krasnopolskyi.security.exception.EntityException;
import com.krasnopolskyi.security.exception.GymException;
import com.krasnopolskyi.security.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RegistrationControllerTest {


    @Mock
    private UserServiceImpl userService;
    @InjectMocks
    private RegistrationController registrationController;

    private ObjectMapper objectMapper;

    private TraineeDto mockTraineeDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockTraineeDto = new TraineeDto("John", "Doe",
                LocalDate.of(2000, 10,10), "address");
    }

    @Test
    void createTrainee() throws GymException {
        // Arrange
        UserCredentials credentials = new UserCredentials("username", "password");
        when(userService.saveTrainee(any(TraineeDto.class))).thenReturn(credentials);


        // Act
        ResponseEntity<UserCredentials> response = registrationController.createTrainee(mockTraineeDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void createTrainer() throws GymException {
        // Arrange
        UserCredentials credentials = new UserCredentials("username", "password");
        when(userService.saveTrainer(any(TrainerDto.class))).thenReturn(credentials);

        TrainerDto trainerDto = new TrainerDto("John", "Doe", 1);

        // Act
        ResponseEntity<UserCredentials> response = registrationController.createTrainer(trainerDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

}
