package com.krasnopolskyi.security.steps;

import com.krasnopolskyi.security.dto.UserCredentials;
import com.krasnopolskyi.security.runners.CucumberSpringConfiguration;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CucumberContextConfiguration
public class SecuritySteps extends CucumberSpringConfiguration {
    private String jwtToken;
    private RestTemplate restTemplate = new RestTemplate();
    private final String loginUrl = "/api/v1/fit-coach/authn/login";
//    private final String loginUrl = "http://localhost:8080/api/v1/fit-coach/authn/login";
    private ResponseEntity<String> response;

    private UserCredentials credentials;

    @Given("the user has valid credentials")
    public void theUserHasValidCredentials() {
        // Setup valid credentials
        credentials = new UserCredentials("usain.bolt", "root");
    }

    @When("the user logs into the system")
    public void theUserLogsIntoTheSystem() {
        // Create a login request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        String body = "{ \"username\": \"user\", \"password\": \"password\" }";

        HttpEntity<UserCredentials> request = new HttpEntity<>(credentials, headers);

        // Uses injected TestRestTemplate to send request
        response = testRestTemplate.postForEntity(loginUrl, request, String.class);
//        // Send POST request to login endpoint
//        response = restTemplate.postForEntity(loginUrl, request, String.class);

        // Extract JWT token from response
        assertNotNull(response.getBody());
        // also I set up JWT token into header Baerer
//        jwtToken = response.getBody(); // Assuming token is in the body for learning

        jwtToken = response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        assertNotNull(response.getBody());
    }

    @Then("the user should receive a valid JWT token")
    public void theUserShouldReceiveAValidJwtToken() {
        // Assert that the token is not null or empty
        assertNotNull(jwtToken);
        assertTrue(jwtToken.startsWith("Bearer ")); // Assuming Bearer token format
    }
}
