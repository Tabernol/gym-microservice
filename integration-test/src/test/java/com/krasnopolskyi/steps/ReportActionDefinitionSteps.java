package com.krasnopolskyi.steps;

import com.krasnopolskyi.config.CucumberSpringConfiguration;
import com.krasnopolskyi.data.Global;
import com.krasnopolskyi.report.entity.ReportTrainer;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ReportActionDefinitionSteps extends CucumberSpringConfiguration {

    private final String getReportUrl = "http://localhost:8765/api/v1/fit-coach/report/generate/{username}";
    private ResponseEntity<ReportTrainer> reportTrainerResponse;


    @When("the user ask report")
    public void theUserAskReport() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(Global.jwtToken);  // Set JWT Token

        // Create HttpEntity with headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Pass HttpEntity with headers as part of the request
        reportTrainerResponse = testRestTemplate.exchange(
                getReportUrl,
                HttpMethod.GET,
                entity,
                ReportTrainer.class,
                Global.TRAINER_USERNAME
        );
    }


    @Then("the user received report")
    public void theUserReceivedReport() {
        assertNotNull(reportTrainerResponse.getBody());
        assertEquals(HttpStatus.OK, reportTrainerResponse.getStatusCode());
        assertEquals(Global.TRAINER_USERNAME, reportTrainerResponse.getBody().getUsername());
    }

    @When("the user ask report for another trainer")
    public void theUserAskReportForAnotherTrainer() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(Global.jwtToken);  // Set JWT Token

        // Create HttpEntity with headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Pass HttpEntity with headers as part of the request
        reportTrainerResponse = testRestTemplate.exchange(
                getReportUrl,
                HttpMethod.GET,
                entity,
                ReportTrainer.class,
                "another.trainer"
        );
    }

    @Then("the user does not received report")
    public void theUserDoesNotReceivedReport() {
        assertNotNull(reportTrainerResponse.getBody());
        assertEquals(HttpStatus.FORBIDDEN, reportTrainerResponse.getStatusCode());
    }
}
