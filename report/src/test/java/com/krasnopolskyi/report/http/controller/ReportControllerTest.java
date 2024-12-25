package com.krasnopolskyi.report.http.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krasnopolskyi.report.entity.TrainingSession;
import com.krasnopolskyi.report.model.ReportTraining;
import com.krasnopolskyi.report.service.ReportService;
import com.krasnopolskyi.report.service.TrainingSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @Mock
    private TrainingSessionService trainingSessionService;

    @InjectMocks
    private ReportController reportController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private TrainingSession trainingSession;
    private ReportTraining reportTraining;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reportController).build();

        // Initialize a sample TrainingSession and ReportTraining for testing
        trainingSession = new TrainingSession();
        trainingSession.setId(1L);
        trainingSession.setDuration(60);

        reportTraining = new ReportTraining();
        reportTraining.setUsername("trainer123");
        reportTraining.setActive(true);
    }

    @Test
    public void testAddTrainingSession() throws Exception {
        // Mock the behavior of trainingSessionService.saveTrainingSession
        when(trainingSessionService.saveTrainingSession(any(TrainingSession.class))).thenReturn(trainingSession);

        // Perform POST request to add training session
        mockMvc.perform(post("/api/v1/fit-coach/report/training-session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingSession)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.duration").value(60));

        // Verify that the service method was called once
        verify(trainingSessionService, times(1)).saveTrainingSession(any(TrainingSession.class));
    }

    @Test
    public void testGetReportByUsername() throws Exception {
        // Mock the behavior of reportService.getReportByUsername
        when(reportService.getReportByUsername("trainer123")).thenReturn(reportTraining);

        // Perform GET request to generate the report by username
        mockMvc.perform(get("/api/v1/fit-coach/report/generate/trainer123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("trainer123"));

        // Verify that the service method was called once
        verify(reportService, times(1)).getReportByUsername("trainer123");
    }
}
