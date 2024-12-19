package com.krasnopolskyi.rest;

import com.krasnopolskyi.fitcoach.dto.request.training.TrainingDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainingResponseDto;
import com.krasnopolskyi.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrainingRestIT extends IntegrationTestBase {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    private String token;

//    @BeforeEach
//    void setUp() {
//        if (token == null) {  // authenticate only if token is not already set
//            UserCredentials credentials = new UserCredentials("john.doe", "root");
//            ResponseEntity<String> response = restTemplate.postForEntity(
//                    "http://localhost:" + port + "/api/v1/fit-coach/authn/login", credentials, String.class);
//
//            token = response.getBody(); // store token
//        }
//
//        // Set Authorization header for subsequent requests
//        restTemplate.getRestTemplate().getInterceptors().add((request, body, execution) -> {
//            request.getHeaders().add("Authorization", "Bearer " + token);
//            return execution.execute(request, body);
//        });
//    }

    @Test
    void addTrainingIT(){
        TrainingDto trainingDto = new TrainingDto(
                "john.doe",
                "usain.bolt",
                "First training",
                LocalDate.now(),
                60);

        ResponseEntity<TrainingResponseDto> response = restTemplate
                .postForEntity("http://localhost:" + port + "/api/v1/fit-coach/trainings", trainingDto, TrainingResponseDto.class);

        assertEquals("First training", response.getBody().trainingName());
        assertEquals("John Doe", response.getBody().traineeFullName());
        assertEquals("Usain Bolt", response.getBody().trainerFullName());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}
