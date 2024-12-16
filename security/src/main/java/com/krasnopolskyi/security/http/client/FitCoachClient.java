package com.krasnopolskyi.security.http.client;

import com.krasnopolskyi.security.dto.TraineeFullDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "fitCoach", url = "http://localhost:8765/api/v1/fit-coach")
public interface FitCoachClient {


    // POST create new trainee
    @PostMapping("/trainees/create")
    ResponseEntity<String> saveTrainee(@RequestBody TraineeFullDto traineeFullDto);
}
