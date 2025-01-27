package com.krasnopolskyi.steps;

import com.krasnopolskyi.config.CucumberSpringConfiguration;
import com.krasnopolskyi.data.Global;
import com.krasnopolskyi.int_test.dto.TrainingDto;
import com.krasnopolskyi.int_test.dto.TrainingResponseDto;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TrainingActionDefinitionsSteps extends CucumberSpringConfiguration {

    private final String addTrainingUrl = "http://localhost:8765/api/v1/fit-coach/trainings";

    private TrainingDto trainingDto;

    private ResponseEntity<TrainingResponseDto> trainingResponseDto;

    @Given("invalid training session user is not exist")
    public void theUserCreateInvalidTrainingSession() {
        trainingDto = new TrainingDto(
                "dontexist.user",
                Global.TRAINER_USERNAME,
                "The first training",
                LocalDate.of(2024, 12, 12),
                60);
    }

    @Given("valid training session")
    public void theUserCreateValidTrainingSession() {
        trainingDto = new TrainingDto(
                Global.TRAINEE_USERNAME,
                Global.TRAINER_USERNAME,
                "The first training",
                LocalDate.of(2024, 12, 12),
                60);
    }


    @Given("invalid training session invalid date")
    public void invalidTrainingSessionInvalidDate() {
        trainingDto = new TrainingDto(
                Global.TRAINEE_USERNAME,
                Global.TRAINER_USERNAME,
                "The first training",
                LocalDate.of(2200, 12, 12),
                60);
    }

    @When("the user create training session")
    public void theUserCreateNewTrainingSession() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(Global.jwtToken);

        HttpEntity<TrainingDto> request = new HttpEntity<>(trainingDto, headers);

        // Uses injected TestRestTemplate to send request
        trainingResponseDto = testRestTemplate.postForEntity(addTrainingUrl, request, TrainingResponseDto.class);
    }

    @Then("the user received success response about training")
    public void theUserReceivedSuccessResponseAboutTraining() {
        assertNotNull(trainingResponseDto.getBody());
        assertEquals(HttpStatus.CREATED, trainingResponseDto.getStatusCode());
        assertEquals("Usain Bolt", trainingResponseDto.getBody().trainerFullName());
    }

    @Then("the user received fail response about training")
    public void theUserReceivedFailResponseAboutTraining() {
        assertNotNull(trainingResponseDto.getBody());
        assertEquals(HttpStatus.NOT_FOUND, trainingResponseDto.getStatusCode());
    }


    @Then("the user received fail response about date training")
    public void theUserReceivedFailResponseAboutDateTraining() {
        assertNotNull(trainingResponseDto.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, trainingResponseDto.getStatusCode());
    }

}
