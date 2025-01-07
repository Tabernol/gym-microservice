package com.krasnopolskyi.fitcoach.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.krasnopolskyi.fitcoach.dto.request.trainee.TraineeDto;
import com.krasnopolskyi.fitcoach.dto.request.trainer.TrainerDto;
import com.krasnopolskyi.fitcoach.entity.User;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import jakarta.jms.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

class UserActionListenerTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private UserService userService;
    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private UserActionListener userActionListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void receiveTraineeMessage_ShouldProcessTraineeMessage() {
        // Arrange
        TraineeDto traineeDto = new TraineeDto();

        // Act
        userActionListener.receiveTraineeMessage(traineeDto);

        // Assert
        verify(traineeService, times(1)).save(traineeDto);
    }

    @Test
    void receiveTraineeMessage_ShouldLogErrorWhenExceptionThrown() {
        // Arrange
        TraineeDto traineeDto = new TraineeDto();
        doThrow(new RuntimeException("Test Exception")).when(traineeService).save(traineeDto);

        // Act
        userActionListener.receiveTraineeMessage(traineeDto);

        // Assert
        verify(traineeService, times(1)).save(traineeDto);
        // We expect no exception to be thrown because the listener handles it internally
    }

    @Test
    void receiveTrainerMessage_ShouldProcessTrainerMessage() throws EntityException {
        // Arrange
        TrainerDto trainerDto = new TrainerDto();

        // Act
        userActionListener.receiveTrainerMessage(trainerDto);

        // Assert
        verify(trainerService, times(1)).save(trainerDto);
    }

    @Test
    void receiveTrainerMessage_ShouldLogErrorWhenExceptionThrown() throws EntityException {
        // Arrange
        TrainerDto trainerDto = new TrainerDto();
        doThrow(new RuntimeException("Test Exception")).when(trainerService).save(trainerDto);

        // Act
        userActionListener.receiveTrainerMessage(trainerDto);

        // Assert
        verify(trainerService, times(1)).save(trainerDto);
        // Exception is logged but not thrown to the caller
    }

    @Test
    void receiveChangeStatusMessage_ShouldProcessUserMessage() throws EntityException {
        // Arrange
        User user = new User();

        // Act
        userActionListener.receiveChangeStatusMessage(user);

        // Assert
        verify(userService, times(1)).updateLocalUser(user);
    }

    @Test
    void receiveChangeStatusMessage_ShouldLogErrorWhenExceptionThrown() throws EntityException {
        // Arrange
        User user = new User();
        doThrow(new RuntimeException("Test Exception")).when(userService).updateLocalUser(user);

        // Act
        userActionListener.receiveChangeStatusMessage(user);

        // Assert
        verify(userService, times(1)).updateLocalUser(user);
        // Exception is logged but not thrown to the caller
    }
}
