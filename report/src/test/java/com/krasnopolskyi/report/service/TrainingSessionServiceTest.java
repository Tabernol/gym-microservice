package com.krasnopolskyi.report.service;

import com.krasnopolskyi.report.entity.TrainingSession;
import com.krasnopolskyi.report.repository.TrainingSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.annotation.EnableJms;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@EnableJms
public class TrainingSessionServiceTest {
    @Mock
    private TrainingSessionRepository trainingSessionRepository;
    @InjectMocks
    private TrainingSessionService trainingSessionService;
    private TrainingSession trainingSession;

    @BeforeEach
    public void setUp() {
        // Initialize the mock objects
        MockitoAnnotations.openMocks(this);

        // Create a sample TrainingSession object for tests
        trainingSession = new TrainingSession();
        trainingSession.setDate(LocalDate.of(2025, 01, 10));
    }

    @Test
    void testReceiveTrainingSessionMessage_successfulSave() throws Exception {
        // Given
        when(trainingSessionRepository.save(any(TrainingSession.class))).thenReturn(trainingSession);

        // When
        trainingSessionService.receiveTrainingSessionMessage(trainingSession);

        // Then
        verify(trainingSessionRepository, times(1)).save(trainingSession);
    }

    @Test
    void testReceiveTrainingSessionMessage_nullDate_shouldThrowIllegalArgumentException() {
        // Given
        trainingSession.setDate(null); // Set date to null to trigger the exception

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            trainingSessionService.receiveTrainingSessionMessage(trainingSession);
        });

        assertEquals("Training session date cannot be null.", exception.getMessage());
    }

    @Test
    void testSaveTrainingSession_databaseSaveFailed_shouldThrowRuntimeException() {
        // Given
        when(trainingSessionRepository.save(any(TrainingSession.class))).thenThrow(new RuntimeException("Database error"));

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            trainingSessionService.saveTrainingSession(trainingSession);
        });

        assertEquals("Database save failed", exception.getMessage());
    }

    @Test
    void testReceiveTrainingSessionMessage_invalidMessage_shouldNotCallSave() {
        // Given
        trainingSession.setDate(null);  // Invalid message

        // When
        assertThrows(IllegalArgumentException.class, () -> {
            trainingSessionService.receiveTrainingSessionMessage(trainingSession);
        });

        // Then
        verify(trainingSessionRepository, never()).save(any(TrainingSession.class)); // Ensure save is not called
    }

}
