package com.krasnopolskyi.fitcoach.http.rest;

import com.krasnopolskyi.fitcoach.dto.request.trainer.TrainerUpdateDto;
import com.krasnopolskyi.fitcoach.dto.request.training.TrainingFilterDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainerProfileDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainingResponseDto;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.GymException;
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
    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileDto> getTrainer(@PathVariable("username") String username) throws EntityException {
        log.info("call controller get Trainer");
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
     * Update trainer fields
     * @param trainerDto Dto
     * @return Dto with other fields
     * @throws EntityException will be throw if trainer does not exist with such username
     */
    @Operation(summary = "Update trainer profile",
            description = "Updates an existing trainer's profile with new information.")
    @PutMapping("/{username}")
    public ResponseEntity<TrainerProfileDto> updateTrainer(
            @PathVariable("username") String username,
            @Validated(Create.class) @RequestBody TrainerUpdateDto trainerDto)
            throws GymException {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainerService.update(username, trainerDto));
    }
}
