package com.krasnopolskyi.fitcoach.http.rest;

import com.krasnopolskyi.fitcoach.entity.TrainingType;
import com.krasnopolskyi.fitcoach.service.TrainingTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class TrainingTypeControllerTest {

    @InjectMocks
    private TrainingTypeController trainingTypeController;

    @Mock
    private TrainingTypeService trainingTypeService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(trainingTypeController).build();
    }

    @Test
    void findAll_ShouldReturnListOfTrainingTypes() throws Exception {
        // Arrange
        List<TrainingType> trainingTypes = Arrays.asList(
                new TrainingType(1, "Strength Training"),
                new TrainingType(2, "Cardio")
        );
        when(trainingTypeService.findAll()).thenReturn(trainingTypes);

        // Act & Assert (MockMvc way)
        mockMvc.perform(get("/api/v1/fit-coach/training-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].type", is("Strength Training")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].type", is("Cardio")));
    }

    @Test
    void findAll_ShouldReturnListOfTrainingTypes_ResponseEntityWay() {
        // Arrange
        List<TrainingType> trainingTypes = Arrays.asList(
                new TrainingType(1, "Strength Training"),
                new TrainingType(2, "Cardio")
        );
        when(trainingTypeService.findAll()).thenReturn(trainingTypes);

        // Act
        ResponseEntity<List<TrainingType>> response = trainingTypeController.findAll();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("Strength Training", response.getBody().get(0).getType());
    }

}
