package com.krasnopolskyi.report.service;

import com.krasnopolskyi.report.entity.TrainingSession;
import com.krasnopolskyi.report.entity.TrainingSessionOperation;
import com.krasnopolskyi.report.model.ReportTraining;
import com.krasnopolskyi.report.model.YearTrainingData;
import com.krasnopolskyi.report.repository.TrainingSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ReportServiceTest {

    @Mock
    private TrainingSessionRepository trainingSessionRepository;

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetReportByUsername_NoData() {
        // Arrange
        String username = "john_doe";
        when(trainingSessionRepository.findAllByUsername(username)).thenReturn(Collections.emptyList());

        // Act
        ReportTraining report = reportService.getReportByUsername(username);

        // Assert
        assertNull(report, "Report should be null if no training sessions are found.");
    }

    @Test
    void testGetReportByUsername_WithData() {
        // Arrange
        String username = "john_doe";

        TrainingSession session1 = new TrainingSession();
        session1.setId(1L);
        session1.setUsername(username);
        session1.setFirstName("John");
        session1.setLastName("Doe");
        session1.setDate(LocalDate.of(2024, Month.JANUARY, 10));
        session1.setDuration(60);
        session1.setActive(true);
        session1.setOperation(TrainingSessionOperation.ADD);

        TrainingSession session2 = new TrainingSession();
        session2.setId(2L);
        session2.setUsername(username);
        session2.setFirstName("John");
        session2.setLastName("Doe");
        session2.setDate(LocalDate.of(2024, Month.FEBRUARY, 20));
        session2.setDuration(90);
        session2.setActive(true);
        session2.setOperation(TrainingSessionOperation.ADD);

        when(trainingSessionRepository.findAllByUsername(username)).thenReturn(Arrays.asList(session1, session2));

        // Act
        ReportTraining report = reportService.getReportByUsername(username);

        // Assert
        assertNotNull(report, "Report should not be null when training sessions are found.");
        assertEquals(username, report.getUsername());
        assertEquals("John", report.getFirstName());
        assertEquals("Doe", report.getLastName());
        assertTrue(report.isActive());
        assertEquals(2, report.getReport().get(0).getYearTrainingData().size(), "Report should have data for 2 months.");
        assertEquals(150, report.getReport().stream().mapToInt(YearTrainingData::getYearSummaryDuration).sum(), "Total duration should be the sum of both sessions.");
    }

    @Test
    void testGetReportByUsername_EmptyMonthExcluded() {
        // Arrange
        String username = "john_doe";

        TrainingSession session1 = new TrainingSession();
        session1.setId(1L);
        session1.setUsername(username);
        session1.setFirstName("John");
        session1.setLastName("Doe");
        session1.setDate(LocalDate.of(2023, Month.JANUARY, 10));
        session1.setDuration(60);
        session1.setActive(true);
        session1.setOperation(TrainingSessionOperation.ADD);

        TrainingSession session2 = new TrainingSession();
        session2.setId(2L);
        session2.setUsername(username);
        session2.setFirstName("John");
        session2.setLastName("Doe");
        session2.setDate(LocalDate.of(2024, Month.MARCH, 15));
        session2.setDuration(120);
        session2.setActive(true);
        session2.setOperation(TrainingSessionOperation.ADD);

        when(trainingSessionRepository.findAllByUsername(username)).thenReturn(Arrays.asList(session1, session2));

        // Act
        ReportTraining report = reportService.getReportByUsername(username);

        // Assert
        assertNotNull(report, "Report should not be null when training sessions are found.");
        assertEquals(2, report.getReport().size(), "There should be data for 2 months even though one month has no data.");
        assertEquals(180, report.getReport().stream().mapToInt(YearTrainingData::getYearSummaryDuration).sum(), "Total duration should exclude empty months.");
    }

}
