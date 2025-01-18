package com.krasnopolskyi.report.service;

import com.krasnopolskyi.report.entity.ReportTrainer;
import com.krasnopolskyi.report.entity.TrainingSession;
import com.krasnopolskyi.report.entity.TrainingSessionOperation;
import com.krasnopolskyi.report.model.Trainer;
import com.krasnopolskyi.report.repository.ReportTrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class ReportServiceTest {
    @Mock
    private ReportTrainerRepository reportTrainerRepository;

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getReportByUsername_shouldReturnReportTrainer_whenFound() {
        // Arrange
        String username = "john.doe";
        ReportTrainer mockReportTrainer = new ReportTrainer(
                "1", username, "John", "Doe", true,
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
        when(reportTrainerRepository.findByUsername(username)).thenReturn(Optional.of(mockReportTrainer));

        // Act
        ReportTrainer result = reportService.getReportByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("John", result.getFirstName());
        verify(reportTrainerRepository, times(1)).findByUsername(username);
    }

    @Test
    void getReportByUsername_shouldThrowException_whenNotFound() {
        // Arrange
        String username = "unknown";
        when(reportTrainerRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reportService.getReportByUsername(username);
        });
        assertEquals("Could not found trainer with " + username, exception.getMessage());
        verify(reportTrainerRepository, times(1)).findByUsername(username);
    }

    @Test
    void saveOrUpdateReport_shouldCreateNewReport_whenTrainerDoesNotExist() {
        // Arrange
        TrainingSession mockTrainingSession = new TrainingSession(1L, "john.doe", "John", "Doe", true, LocalDate.of(2024, Month.JANUARY, 5), 60, TrainingSessionOperation.ADD);
        when(reportTrainerRepository.findByUsername(mockTrainingSession.getUsername())).thenReturn(Optional.empty());

        ReportTrainer mockNewReportTrainer = new ReportTrainer(
                "1", mockTrainingSession.getUsername(), mockTrainingSession.getFirstName(),
                mockTrainingSession.getLastName(), mockTrainingSession.isActive(),
                List.of(new ReportTrainer.YearTrainingData(
                        2024,
                        List.of(new ReportTrainer.MonthTrainingData(
                                Month.JANUARY,
                                List.of(new ReportTrainer.SingleTrainingData(
                                        1L, LocalDate.of(2024, Month.JANUARY, 5), 60, TrainingSessionOperation.ADD
                                )),
                                60
                        ))
                ))
        );
        when(reportTrainerRepository.save(any(ReportTrainer.class))).thenReturn(mockNewReportTrainer);

        // Act
        ReportTrainer result = reportService.saveOrUpdateReport(mockTrainingSession);

        // Assert
        assertNotNull(result);
        assertEquals(mockTrainingSession.getUsername(), result.getUsername());
        assertEquals(mockTrainingSession.getFirstName(), result.getFirstName());
        verify(reportTrainerRepository, times(1)).findByUsername(mockTrainingSession.getUsername());
        verify(reportTrainerRepository, times(1)).save(any(ReportTrainer.class));
    }

    @Test
    void saveOrUpdateReport_shouldUpdateExistingReport_whenTrainerExists() {
        List trainingData = new ArrayList();
        List yearData = new ArrayList();
        List monthData = new ArrayList();
        ReportTrainer.SingleTrainingData singleTrainingData = new ReportTrainer.SingleTrainingData(
                1L, LocalDate.of(2024, Month.JANUARY, 5), 60, TrainingSessionOperation.ADD
        );
        trainingData.add(singleTrainingData);

        ReportTrainer.MonthTrainingData monthTrainingData = new ReportTrainer.MonthTrainingData(
                Month.JANUARY,
                trainingData,
                60
        );
        monthData.add(monthTrainingData);

        ReportTrainer.YearTrainingData yearTrainingData = new ReportTrainer.YearTrainingData(
                2024,
                monthData);

        yearData.add(yearTrainingData);




        // Arrange
        TrainingSession mockTrainingSession = new TrainingSession(1L, "john.doe", "John", "Doe", true, LocalDate.of(2024, Month.JANUARY, 5), 60, TrainingSessionOperation.ADD);
        ReportTrainer existingTrainer = new ReportTrainer(
                "1", mockTrainingSession.getUsername(), mockTrainingSession.getFirstName(),
                mockTrainingSession.getLastName(), mockTrainingSession.isActive(), yearData);
        when(reportTrainerRepository.findByUsername(mockTrainingSession.getUsername())).thenReturn(Optional.of(existingTrainer));
        when(reportTrainerRepository.save(any(ReportTrainer.class))).thenReturn(existingTrainer);

        // Act
        ReportTrainer result = reportService.saveOrUpdateReport(mockTrainingSession);

        // Assert
        assertNotNull(result);
        assertEquals(mockTrainingSession.getUsername(), result.getUsername());
        verify(reportTrainerRepository, times(1)).findByUsername(mockTrainingSession.getUsername());
        verify(reportTrainerRepository, times(1)).save(any(ReportTrainer.class));
    }

    @Test
    void updateTrainer_shouldUpdateTrainer_whenTrainerExists() {
        // Arrange
        Trainer mockTrainer = new Trainer("john.doe", "John", "Doe", true);
        ReportTrainer existingTrainer = new ReportTrainer("1", "john.doe", "OldFirstName", "OldLastName", false, null);

        when(reportTrainerRepository.findByUsername(mockTrainer.getUsername())).thenReturn(Optional.of(existingTrainer));
        when(reportTrainerRepository.save(any(ReportTrainer.class))).thenReturn(existingTrainer);

        // Act
        ReportTrainer result = reportService.updateTrainer(mockTrainer);

        // Assert
        assertNotNull(result);
        assertEquals(mockTrainer.getFirstName(), result.getFirstName());
        assertEquals(mockTrainer.getLastName(), result.getLastName());
        assertTrue(result.isActive());
        verify(reportTrainerRepository, times(1)).findByUsername(mockTrainer.getUsername());
        verify(reportTrainerRepository, times(1)).save(existingTrainer);
    }

    @Test
    void updateTrainer_shouldReturnNull_whenTrainerDoesNotExist() {
        // Arrange
        Trainer mockTrainer = new Trainer("unknown", "John", "Doe", true);
        when(reportTrainerRepository.findByUsername(mockTrainer.getUsername())).thenReturn(Optional.empty());

        // Act
        ReportTrainer result = reportService.updateTrainer(mockTrainer);

        // Assert
        assertNull(result);
        verify(reportTrainerRepository, times(1)).findByUsername(mockTrainer.getUsername());
        verify(reportTrainerRepository, times(0)).save(any(ReportTrainer.class));
    }

}
