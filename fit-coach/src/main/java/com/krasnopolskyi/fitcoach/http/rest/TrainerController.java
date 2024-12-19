package com.krasnopolskyi.fitcoach.http.rest;

import com.krasnopolskyi.fitcoach.dto.request.trainee.TraineeFullDto;
import com.krasnopolskyi.fitcoach.dto.request.trainer.TrainerDto;
import com.krasnopolskyi.fitcoach.dto.request.trainer.TrainerFullDto;
import com.krasnopolskyi.fitcoach.dto.request.trainer.TrainerUpdateDto;
import com.krasnopolskyi.fitcoach.dto.request.training.TrainingFilterDto;
import com.krasnopolskyi.fitcoach.dto.request.user.ToggleStatusDto;
import com.krasnopolskyi.fitcoach.dto.request.user.UserCredentials;
import com.krasnopolskyi.fitcoach.dto.response.TrainerProfileDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainingResponseDto;
import com.krasnopolskyi.fitcoach.entity.Trainer;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.GymException;
import com.krasnopolskyi.fitcoach.exception.ValidateException;
import com.krasnopolskyi.fitcoach.http.metric.TrackCountMetric;
import com.krasnopolskyi.fitcoach.service.TrainerService;
import com.krasnopolskyi.fitcoach.validation.Create;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fit-coach/trainers")
@RequiredArgsConstructor
@Slf4j
public class TrainerController {

    private final TrainerService trainerService;

    /**
     * Provides end-point for retrieve data about trainer
     * @param username is unique name of trainer
     * @return to with data about trainer
     * @throws EntityException will be throw if trainer does not exist with such username
     */
    @Operation(summary = "Get trainer profile by username",
            description = "Fetches the profile information of a trainer based on the provided username.")
//    @PreAuthorize("hasAuthority('TRAINER')")
    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileDto> getTrainer(@PathVariable("username") String username) throws EntityException {
        return ResponseEntity.status(HttpStatus.OK).body(trainerService.findByUsername(username));
    }

    /**
     * provides filtering functionality for training sessions of trainer
     * @param username target trainer for searching
     * @param periodFrom date from
     * @param periodTo date to
     * @param partner trainee
     * @return List of trainings otherwise empty list
     * @throws EntityException will be thrown if target username does not exist as trainer
     */
    @Operation(summary = "Filter trainings by trainer",
            description = "Filters the training sessions for a trainer based on optional parameters like period and partner (trainee).")
//    @PreAuthorize("hasAuthority('TRAINER')")
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainingResponseDto>> findTraining(
            @PathVariable String username,
            @RequestParam(required = false) LocalDate periodFrom,
            @RequestParam(required = false) LocalDate periodTo,
            @RequestParam(required = false) String partner) throws EntityException {

        TrainingFilterDto filter = TrainingFilterDto.builder()
                .owner(username)
                .startDate(periodFrom)
                .endDate(periodTo)
                .partner(partner)
                .build();

        List<TrainingResponseDto> trainings = trainerService.getTrainings(filter);
        return ResponseEntity.status(HttpStatus.OK).body(trainings);
    }

    /**
     * Provides public end-point for creating trainer
     * @param trainerDto dto with user fields
     * @return credentials for authentication generated username and password
     * @throws EntityException if training type does not exist
     */
    @Operation(summary = "Create a new trainer",
            description = "Creates a new trainer profile, returning the generated credentials for authentication.")
    @PostMapping("/create")
    @TrackCountMetric(name = "api_trainer_create",
            description = "Number of requests to /api/v1/trainers/public endpoint")
    public ResponseEntity<Trainer> createTrainer(
            @Validated(Create.class) @RequestBody TrainerFullDto trainerDto) throws EntityException {
        log.info("attempt to create TRAINER");
        return ResponseEntity.status(HttpStatus.CREATED).body(trainerService.save(trainerDto));
    }

    /**
     * Update trainer fields
     * @param trainerDto Dto
     * @return Dto with other fields
     * @throws EntityException will be throw if trainer does not exist with such username
     */
    @Operation(summary = "Update trainer profile",
            description = "Updates an existing trainer's profile with new information.")
//    @PreAuthorize("hasAuthority('TRAINER')")
    @PutMapping("/{username}")
    public ResponseEntity<TrainerProfileDto> updateTrainer(
            @PathVariable("username") String username,
            @Validated(Create.class) @RequestBody TrainerUpdateDto trainerDto)
            throws GymException {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainerService.update(username, trainerDto));
    }

    /**
     * Provides functionality for changing trainer status
     * @param username of target trainer
     * @param statusDto dto with username and status
     * @return message of result this action
     * @throws EntityException if username does not exist
     * @throws ValidateException if username in pathVariable and in body are different
     */
    @Operation(summary = "Toggle trainer status",
            description = "Toggles the active status of a trainer based on the provided username and status information.")
//    @PreAuthorize("hasAuthority('TRAINER')")
    @PatchMapping("/{username}/toggle-status")
    public ResponseEntity<String> toggleStatus(
            @PathVariable("username") String username,
            @Validated(Create.class) @RequestBody ToggleStatusDto statusDto) throws EntityException, ValidateException {
        return ResponseEntity.status(HttpStatus.OK).body(trainerService.changeStatus(username, statusDto));
    }
}
