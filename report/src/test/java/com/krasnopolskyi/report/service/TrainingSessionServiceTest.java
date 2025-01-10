package com.krasnopolskyi.report.service;

import com.krasnopolskyi.report.entity.TrainingSession;
import com.krasnopolskyi.report.repository.TrainingSessionRepository;
import jakarta.jms.JMSException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TrainingSessionServiceTest {

    @Mock
    private TrainingSessionRepository trainingSessionRepository;

    @InjectMocks
    private TrainingSessionService trainingSessionService;

    private TrainingSession trainingSession;

    @BeforeEach
    public void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Create a dummy TrainingSession object
        trainingSession = new TrainingSession();
        trainingSession.setId(1L);
        trainingSession.setDuration(60);
    }

    @Test
    void receiveTrainingSessionMessage_ShouldSaveTrainingSession() throws JMSException {
        // Arrange
        TrainingSession trainingSession = new TrainingSession();
        trainingSession.setId(1L); // Example training session setup
        trainingSession.setDuration(90);

        // Act
        trainingSessionService.receiveTrainingSessionMessage(trainingSession);

        // Assert
        verify(trainingSessionRepository, times(1)).save(trainingSession);
    }

    @Test
    void receiveTrainingSessionMessage_ShouldLogError_WhenSaveFails() throws JMSException {
        // Arrange
        TrainingSession trainingSession = new TrainingSession();
        trainingSession.setId(1L); // Example training session setup
        trainingSession.setDuration(90);

        doThrow(new RuntimeException("Database save failed")).when(trainingSessionRepository).save(any(TrainingSession.class));

        // Act
        trainingSessionService.receiveTrainingSessionMessage(trainingSession);

        // Assert
        verify(trainingSessionRepository, times(1)).save(trainingSession);
    }
}
