package com.krasnopolskyi.security.http.client;

import com.krasnopolskyi.security.dto.TraineeFullDto;
import com.krasnopolskyi.security.dto.TrainerFullDto;
import com.krasnopolskyi.security.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "fitCoach", url = "http://localhost:8765/api/v1/fit-coach")
public interface FitCoachClient {


    // POST create new trainee
    @PostMapping("/trainees/create")
    ResponseEntity<?> saveTrainee(@RequestBody TraineeFullDto traineeFullDto);


    // POST create new trainer
    @PostMapping("/trainers/create")
    ResponseEntity<?> saveTrainer(@RequestBody TrainerFullDto trainerFullDto);

    @PutMapping("/users")
    ResponseEntity<?> updateUser(@RequestBody UserDto userDto);
}

