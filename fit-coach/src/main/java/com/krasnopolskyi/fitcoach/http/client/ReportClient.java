package com.krasnopolskyi.fitcoach.http.client;

import com.krasnopolskyi.fitcoach.dto.request.training.TrainingSessionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "report", url = "http://localhost:8765/api/v1/fit-coach/report")
public interface ReportClient {
    // POST request to add a new training
    @PostMapping("/training-session")
    ResponseEntity<TrainingSessionDto> saveTrainingSession(@RequestBody TrainingSessionDto trainingSessionDto);
}
