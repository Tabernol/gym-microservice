package com.krasnopolskyi.report.service;

import com.krasnopolskyi.report.entity.TrainingSession;
import com.krasnopolskyi.report.repository.TrainingSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    public void testSaveTrainingSession() {
        // Mock repository's save method to return the training session
        when(trainingSessionRepository.save(any(TrainingSession.class))).thenReturn(trainingSession);

        // Call the service method
        TrainingSession savedSession = trainingSessionService.saveTrainingSession(trainingSession);

        // Verify that the save method was called once
        verify(trainingSessionRepository, times(1)).save(trainingSession);

        // Assert that the returned session matches the input session
        assertNotNull(savedSession);
        assertEquals(trainingSession.getId(), savedSession.getId());
        assertEquals(trainingSession.getDuration(), savedSession.getDuration());
    }
}
