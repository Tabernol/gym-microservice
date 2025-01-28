package com.krasnopolskyi.steps;

import com.krasnopolskyi.config.CucumberSpringConfiguration;
import com.krasnopolskyi.data.Global;
import com.krasnopolskyi.int_test.dto.*;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration
public class UserActionDefinitionsSteps extends CucumberSpringConfiguration {
    private final String loginUrl = "http://localhost:8765/api/v1/fit-coach/auth/login";
    private final String registerTrainerUrl = "http://localhost:8765/api/v1/fit-coach/auth/sign-up/trainer";
    private final String changePasswordUrl = "http://localhost:8765/api/v1/fit-coach/auth/pass/change";
    private final String trainerUrl = "http://localhost:8765/api/v1/fit-coach/trainers/{username}";
    private ResponseEntity<UserCredentials> createTrainerResponse;
    private ResponseEntity<String> loginResponse;

    ResponseEntity<TrainerProfileDto> updatedTrainer;
    private UserCredentials credentials;
    private TrainerDto trainerDto;

    private TrainerUpdateDto trainerUpdateDto;

    @Given("the user has valid credentials")
    public void theUserHasValidCredentials() {
        // Setup valid credentials
        credentials = new UserCredentials(Global.TRAINER_USERNAME, Global.PASSWORD);
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
        Global.jwtToken = loginResponse.getBody(); // Assuming token is in the body for learning

//        jwtToken = response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        assertNotNull(loginResponse.getBody());
    }

    @Then("the user should receive a valid JWT token")
    public void theUserShouldReceiveAValidJwtToken() {
        // Assert that the token is not null or empty
//        System.out.println("username: " + credentials.username());
        assertNotNull(Global.jwtToken);
        assertTrue(jwtService.isTokenValid(Global.jwtToken, credentials.username()));
    }

    @When("the user sends request to create trainer")
    public void theUserCreateNewAccountTrainer() {

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
        headers.setBearerAuth(Global.jwtToken);

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

    @After
    public void doSomethingAfter(Scenario scenario){
        if(scenario.getName().equals("User login in system and change password then login again")){
            System.out.println("roll back to old password");
            // Create a login request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(Global.jwtToken);

            ChangePasswordDto changePasswordDto =
                    new ChangePasswordDto(credentials.username(), credentials.password(), Global.PASSWORD);

            // Uses injected TestRestTemplate to send request
            ResponseEntity<String> changePasswordResponse =
                    testRestTemplate.exchange(
                            changePasswordUrl,
                            HttpMethod.PUT,
                            new HttpEntity<>(changePasswordDto, headers),
                            String.class);

            assertNotNull(changePasswordResponse.getBody());
            String body = changePasswordResponse.getBody();
            assertEquals("Password has changed", body);

            credentials = new UserCredentials(credentials.username(), Global.PASSWORD);
        }

        if(scenario.getName().equals("User login in system and change firstname and lastname") ||
                scenario.getName().equals("User login in system and change status to false and fail creating training session")){
            trainerUpdateDto = new TrainerUpdateDto(
                    Global.TRAINER_USERNAME,
                    "Usain",
                    "Bolt",
                    "Cardio",
                    true
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(Global.jwtToken);

            // Uses injected TestRestTemplate to send request

            updatedTrainer = testRestTemplate.exchange(
                    trainerUrl,
                    HttpMethod.PUT,
                    new HttpEntity<>(trainerUpdateDto, headers),
                    TrainerProfileDto.class,
                    Global.TRAINER_USERNAME);
        }
    }

    @When("the trainer change firstname and lastname")
    public void theUserChangeFirstnameAndLastname() {

        trainerUpdateDto = new TrainerUpdateDto(
                Global.TRAINER_USERNAME,
                "newName",
                "newLastname",
                "Cardio",
                true
                );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(Global.jwtToken);

        // Uses injected TestRestTemplate to send request

        updatedTrainer = testRestTemplate.exchange(
                trainerUrl,
                HttpMethod.PUT,
                new HttpEntity<>(trainerUpdateDto, headers),
                TrainerProfileDto.class,
                Global.TRAINER_USERNAME);

    }

    @Then("the firstname and lastname were changed")
    public void theFirstnameAndLastnameWereChanged() {
        assertEquals("newName", updatedTrainer.getBody().firstName());
        assertEquals("newLastname", updatedTrainer.getBody().lastName());
    }

    @When("the user change status")
    public void theUserChangeStatus() {
        trainerUpdateDto = new TrainerUpdateDto(
                Global.TRAINER_USERNAME,
                "Usain",
                "Bolt",
                "Cardio",
                false
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(Global.jwtToken);

        // Uses injected TestRestTemplate to send request

        updatedTrainer = testRestTemplate.exchange(
                trainerUrl,
                HttpMethod.PUT,
                new HttpEntity<>(trainerUpdateDto, headers),
                TrainerProfileDto.class,
                Global.TRAINER_USERNAME);
    }

    @Then("the status is changed")
    public void theStatusIsChanged() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(Global.jwtToken);  // Set JWT Token

        // Create HttpEntity with headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Pass HttpEntity with headers as part of the request
        ResponseEntity<TrainerProfileDto> exchange = testRestTemplate.exchange(
                trainerUrl,
                HttpMethod.GET,
                entity,
                TrainerProfileDto.class,
                Global.TRAINER_USERNAME
        );
        assertEquals(HttpStatus.OK, exchange.getStatusCode());
        assertNotNull(exchange.getBody());
        assertEquals(false, exchange.getBody().isActive());
    }
}
