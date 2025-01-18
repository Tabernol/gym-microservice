package com.krasnopolskyi.report.http.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krasnopolskyi.report.entity.ReportTrainer;
import com.krasnopolskyi.report.entity.TrainingSession;
import com.krasnopolskyi.report.entity.TrainingSessionOperation;
import com.krasnopolskyi.report.service.ReportService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getReportByUsername_shouldReturnReportTrainer() {
        // Arrange
        String username = "john.doe";

        // Mock data to return from the service
        ReportTrainer mockReport = new ReportTrainer(
                "1",
                "john.doe",
                "John",
                "Doe",
                true,
                List.of(new ReportTrainer.YearTrainingData(
                        2024,
                        List.of(new ReportTrainer.MonthTrainingData(
                                Month.JANUARY,
                                List.of(new ReportTrainer.SingleTrainingData(
                                        1L,
                                        LocalDate.of(2024, Month.JANUARY, 5),
                                        60,
                                        TrainingSessionOperation.ADD
                                )),
                                60
                        ))
                ))
        );

        when(reportService.getReportByUsername(username)).thenReturn(mockReport);

        // Act
        ResponseEntity<ReportTrainer> response = reportController.getReportByUsername(username);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("john.doe", response.getBody().getUsername());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("Doe", response.getBody().getLastName());
        assertTrue(response.getBody().isActive());
        assertEquals(2024, response.getBody().getYearsData().get(0).getYear());
        assertEquals(Month.JANUARY, response.getBody().getYearsData().get(0).getMonthsData().get(0).getMonth());
        assertEquals(1L, response.getBody().getYearsData().get(0).getMonthsData().get(0).getTrainingSessions().get(0).getSessionId());
        assertEquals(60, response.getBody().getYearsData().get(0).getMonthsData().get(0).getTrainingSessions().get(0).getDuration());

        // Verify that the service method was called once
        verify(reportService, times(1)).getReportByUsername(username);
    }
}
