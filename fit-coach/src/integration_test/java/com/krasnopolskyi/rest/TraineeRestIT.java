package com.krasnopolskyi.rest;

import com.krasnopolskyi.fitcoach.dto.request.UserCredentials;
import com.krasnopolskyi.fitcoach.dto.response.TraineeProfileDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainerProfileShortDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainingResponseDto;
import com.krasnopolskyi.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TraineeRestIT extends IntegrationTestBase {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    private String token;

    @BeforeEach
    void setUp() {
        if (token == null) {  // authenticate only if token is not already set
            UserCredentials credentials = new UserCredentials("john.doe", "root");
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://localhost:" + port + "/api/v1/fit-coach/authn/login", credentials, String.class);

            token = response.getBody(); // store token
        }

        // Set Authorization header for subsequent requests
        restTemplate.getRestTemplate().getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + token);
            return execution.execute(request, body);
        });
    }

    @Test
    void getTraineeTrainingsIT() {
        String username = "john.doe";

        // Create a ParameterizedTypeReference to represent the List<TrainingResponseDto>
        ParameterizedTypeReference<List<TrainingResponseDto>> responseType =
                new ParameterizedTypeReference<List<TrainingResponseDto>>() {
                };

        // Use exchange method instead of getForEntity to handle the generic type
        ResponseEntity<List<TrainingResponseDto>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/fit-coach/trainees/{username}/trainings",
                HttpMethod.GET,
                null,
                responseType,
                username);

        // Assertions (adjust as necessary)
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getTraineeIT() {
        String username = "john.doe";

        // Use exchange method instead of getForEntity to handle the generic type
        ResponseEntity<TraineeProfileDto> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/fit-coach/trainees/{username}",
                HttpMethod.GET,
                null,
                TraineeProfileDto.class,
                username);

        // Assertions (adjust as necessary)
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
    }


//    @Test
//    void deleteTraineeIT() {
//        String username = "serena.williams";
//
//        // Use exchange method instead of getForEntity to handle the generic type
//        ResponseEntity<?> response = restTemplate.exchange(
//                "http://localhost:" + port + "/api/v1/fit-coach/trainees/{username}",
//                HttpMethod.DELETE,
//                null,
//                Void.class,
//                username);
//
//        // Assertions (adjust as necessary)
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//    }

    @Test
    void updateTrainersIT(){
        String username = "john.doe";
        List<String> trainers = new ArrayList<>();
        trainers.add("usain.bolt");
        trainers.add("arnold.schwarzenegger");


        ParameterizedTypeReference<List<TrainerProfileShortDto>> responseType =
                new ParameterizedTypeReference<List<TrainerProfileShortDto>>() {
                };

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<String>> entity = new HttpEntity<>(trainers, headers);


        ResponseEntity<List<TrainerProfileShortDto>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/fit-coach/trainees/{username}/trainers/update",
                HttpMethod.PUT,
                entity,
                responseType,
                username);

        assertEquals(2, response.getBody().size());

    }
}
