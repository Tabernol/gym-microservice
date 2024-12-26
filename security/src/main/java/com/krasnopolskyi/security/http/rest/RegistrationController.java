package com.krasnopolskyi.security.http.rest;

import com.krasnopolskyi.security.dto.TraineeDto;
import com.krasnopolskyi.security.dto.TrainerDto;
import com.krasnopolskyi.security.dto.UserCredentials;
import com.krasnopolskyi.security.exception.EntityException;
import com.krasnopolskyi.security.exception.GymException;
import com.krasnopolskyi.security.service.UserService;
import com.krasnopolskyi.security.utils.validation.Create;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fit-coach/auth/sign-up")
@Slf4j
public class RegistrationController {

    private final UserService userService;

    /**
     * Provides public end-point for creating trainee
     *
     * @param traineeDto dto with user fields
     * @return credentials for authentication generated username and password
     */
//    @Operation(summary = "Create a new trainee",
//            description = "Creates a new trainee and returns the generated username and password for authentication.")
    @PostMapping("/trainee")
    @ResponseStatus(HttpStatus.CREATED)
//    @TrackCountMetric(name = "api_trainee_create",
//            description = "Number of requests to /api/v1/trainees/public endpoint")
    public ResponseEntity<UserCredentials> createTrainee(
            @Validated(Create.class) @RequestBody TraineeDto traineeDto) throws GymException {
        log.info("Start create trainee");
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveTrainee(traineeDto));
    }

    /**
     * Provides public end-point for creating trainer
     * @param trainerDto dto with user fields
     * @return credentials for authentication generated username and password
     * @throws EntityException if training type does not exist
     */
//    @Operation(summary = "Create a new trainer",
//            description = "Creates a new trainer profile, returning the generated credentials for authentication.")
    @PostMapping("/trainer")
//    @TrackCountMetric(name = "api_trainer_create",
//            description = "Number of requests to /api/v1/trainers/public endpoint")
    public ResponseEntity<UserCredentials> createTrainer(
            @Validated(Create.class) @RequestBody TrainerDto trainerDto) throws GymException {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveTrainer(trainerDto));
    }
}
