package com.krasnopolskyi.security.steps;

import com.krasnopolskyi.security.dto.UserCredentials;
import com.krasnopolskyi.security.runners.CucumberSpringConfiguration;
import com.krasnopolskyi.security.service.JwtService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CucumberContextConfiguration
public class SecuritySteps extends CucumberSpringConfiguration {
    private String jwtToken;
    private final String loginUrl = "/api/v1/fit-coach/auth/login";
    private ResponseEntity<String> response;
    private UserCredentials credentials;
    @Autowired
    private JwtService jwtService;

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
        testRestTemplate.postForEntity(loginUrl, request, String.class);

        // Extract JWT token from response
        assertNotNull(response.getBody());
        // also I set up JWT token into header Baerer
        jwtToken = response.getBody(); // Assuming token is in the body for learning
        System.out.println("JWT:" + jwtToken);

//        jwtToken = response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        assertNotNull(response.getBody());
    }

    @Then("the user should receive a valid JWT token")
    public void theUserShouldReceiveAValidJwtToken() {
        System.out.println("JWT:" + jwtToken);
        // Assert that the token is not null or empty
        assertNotNull(jwtToken);
        assertTrue(jwtService.isTokenValid(jwtToken, credentials.username()));
    }
}
