package com.krasnopolskyi.security.integration_tests.steps;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.krasnopolskyi.security.dto.ChangePasswordDto;
import com.krasnopolskyi.security.dto.TrainerDto;
import com.krasnopolskyi.security.dto.UserCredentials;
import com.krasnopolskyi.security.integration_tests.config.CucumberSpringConfiguration;
import com.krasnopolskyi.security.integration_tests.mocks.TrainerMock;
import com.krasnopolskyi.security.service.JwtService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration
public class UserActionDefinitionsSteps extends CucumberSpringConfiguration {
    private String jwtToken;
    private final String loginUrl = "/api/v1/fit-coach/auth/login";
    private final String registerTrainerUrl = "/api/v1/fit-coach/auth/sign-up/trainer";
    private final String changePasswordUrl = "/api/v1/fit-coach/auth/pass/change";
    private ResponseEntity<UserCredentials> createTrainerResponse;
    private ResponseEntity<String> loginResponse;
    private UserCredentials credentials;
    @Autowired
    private JwtService jwtService;
    private TrainerDto trainerDto;
    @Autowired
    private WireMockServer wireMockServer;

    @Given("the user has valid credentials")
    public void theUserHasValidCredentials() {
        // Setup valid credentials
        credentials = new UserCredentials("usain.bolt", "root");
    }

    @Given("the user has valid form for creating trainer")
    public void theUserCreateValidTrainer() {
        trainerDto = new TrainerDto("Henry", "Ford", 1);
    }

    @When("the user logs into the system")
    public void theUserLogsIntoTheSystem() {
        // Create a login request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        System.out.println("LOG INTO THE SYSTEM");
        System.out.println("Username: " + credentials.username());
        System.out.println("Pass: " + credentials.password());

        HttpEntity<UserCredentials> request = new HttpEntity<>(credentials, headers);

        // Uses injected TestRestTemplate to send request
        loginResponse = testRestTemplate.postForEntity(loginUrl, request, String.class);

        // Extract JWT token from response
        assertNotNull(loginResponse.getBody());
        jwtToken = loginResponse.getBody(); // Assuming token is in the body for learning

//        jwtToken = response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        assertNotNull(loginResponse.getBody());
    }

    @Then("the user should receive a valid JWT token")
    public void theUserShouldReceiveAValidJwtToken() {
        // Assert that the token is not null or empty
//        System.out.println("username: " + credentials.username());
        assertNotNull(jwtToken);
        assertTrue(jwtService.isTokenValid(jwtToken, credentials.username()));
    }


    @When("the user sends request to create trainer")
    public void theUserCreateNewAccountTrainer() {
        // Set up WireMock stub for the request
        TrainerMock.setupMockTrainerResponse(wireMockServer);

        // Prepare request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TrainerDto> request = new HttpEntity<>(trainerDto, headers);

        // Uses injected TestRestTemplate to send request
        createTrainerResponse = testRestTemplate.postForEntity(registerTrainerUrl, request, UserCredentials.class);

        assertNotNull(createTrainerResponse.getBody());

        credentials = createTrainerResponse.getBody();
    }

    @Then("the user received credentials")
    public void theUserShouldReceiveCredentials() {
        // Assert that the token is not null or empty
//        System.out.println("username " + credentials.username());
//        System.out.println("pass " + credentials.password());
        assertNotNull(credentials);
//        assertEquals("henry.ford", credentials.username());
        assertNotNull(credentials.password());
        assertEquals(10, credentials.password().length());
    }

    @When("the user sends request to change password")
    public void theUserWantsToChangePassword() {

        String password = "newPassword";
        // Create a login request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        System.out.println("JWT: " + jwtToken);
        headers.setBearerAuth(jwtToken);

        ChangePasswordDto changePasswordDto =
                new ChangePasswordDto(credentials.username(), credentials.password(), password);

        // Uses injected TestRestTemplate to send request
        ResponseEntity<String> changePasswordResponse =
                testRestTemplate.exchange(
                        changePasswordUrl,
                        HttpMethod.PUT,
                        new HttpEntity<>(changePasswordDto, headers),
                        String.class);

        // Assert the response message
        assertNotNull(changePasswordResponse.getBody());
        String body = changePasswordResponse.getBody();
        assertEquals("Password has changed", body);

        credentials = new UserCredentials(credentials.username(), password);
    }


}
