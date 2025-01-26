package com.krasnopolskyi.report.service;

import com.krasnopolskyi.report.entity.TrainingSession;
import com.krasnopolskyi.report.model.Trainer;
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
public class CustomEventListenerTest {
    @Mock
    private ReportService reportService;
    @InjectMocks
    private CustomEventListener customEventListener;
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
    void testReceiveTrainingSessionMessage_nullDate_shouldThrowIllegalArgumentException() {
        // Given
        trainingSession.setDate(null); // Set date to null to trigger the exception

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            customEventListener.receiveTrainingSessionMessage(trainingSession);
        });

        assertEquals("Training session date cannot be null.", exception.getMessage());
        verify(reportService, never()).saveOrUpdateReport(trainingSession);
    }

    @Test

    void onUserUpdatedTest(){
        Trainer trainer = new Trainer();

        customEventListener.onUserUpdated(trainer);

        verify(reportService, times(1)).updateTrainer(trainer);
    }

}
