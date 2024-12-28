package com.krasnopolskyi.report.service;

import com.krasnopolskyi.report.entity.TrainingSession;
import com.krasnopolskyi.report.entity.TrainingSessionOperation;
import com.krasnopolskyi.report.http.client.FitCoachClient;
import com.krasnopolskyi.report.model.ReportTraining;
import com.krasnopolskyi.report.model.Trainer;
import com.krasnopolskyi.report.repository.TrainingSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.springframework.http.ResponseEntity;
import feign.FeignException;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ReportServiceTest {

    @Mock
    private TrainingSessionRepository trainingSessionRepository;

    @Mock
    private FitCoachClient fitCoachClient;

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetReportByUsername_Success() {
        // Arrange
        Trainer mockTrainer = new Trainer("john_doe", "John", "Doe", true);
        ResponseEntity<Trainer> mockResponse = ResponseEntity.ok(mockTrainer);
        when(fitCoachClient.getTrainer(anyString())).thenReturn(mockResponse);
        when(trainingSessionRepository.findAllByUsernameAndOperation("john_doe", TrainingSessionOperation.ADD))
                .thenReturn(mockTrainingSessions());

        // Act
        ReportTraining result = reportService.getReportByUsername("john_doe");

        // Assert
        assertEquals(mockTrainer, result.getTrainer());
        assertEquals(1, result.getReport().size()); // One year of training data
    }

    @Test
    void testGetReportByUsername_TrainerNotFound() {
        // Arrange
        when(fitCoachClient.getTrainer(anyString())).thenThrow(FeignException.class);
        when(trainingSessionRepository.findAllByUsernameAndOperation("john_doe", TrainingSessionOperation.ADD))
                .thenReturn(mockTrainingSessions());

        // Act
        ReportTraining result = reportService.getReportByUsername("john_doe");

        // Assert
        assertEquals("john_doe", result.getTrainer().getUsername());
        assertEquals("Unknown", result.getTrainer().getFirstName());
        assertEquals("Unknown", result.getTrainer().getLastName());
    }

    @Test
    void testGetSessionsByUsername_NoSessions() {
        // Arrange
        when(trainingSessionRepository.findAllByUsernameAndOperation(anyString(), Mockito.any()))
                .thenReturn(Collections.emptyList());
        when(fitCoachClient.getTrainer(anyString()))
                .thenReturn(ResponseEntity.ok(new Trainer("john.doe", "unknown", "unknown", true)));


        // Act
        ReportTraining result = reportService.getReportByUsername("john.doe");

        // Assert
        assertEquals(0, result.getReport().size());
    }

    private List<TrainingSession> mockTrainingSessions() {
        return Arrays.asList(
                new TrainingSession(1L, "john_doe", LocalDate.of(2024, Month.JANUARY, 10), 60, TrainingSessionOperation.ADD),
                new TrainingSession(2L, "john_doe", LocalDate.of(2024, Month.FEBRUARY, 15), 45, TrainingSessionOperation.ADD)
        );
    }
}
