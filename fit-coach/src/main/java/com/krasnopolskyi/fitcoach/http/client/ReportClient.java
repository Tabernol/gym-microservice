package com.krasnopolskyi.fitcoach.http.client;

import com.krasnopolskyi.fitcoach.dto.request.training.TrainingSessionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "report", url = "http://localhost:8765/api/v1/report")
public interface ReportClient {

    // GET request to fetch
    @GetMapping("/test")
    String testString();

    // POST request to add a new training
    @PostMapping("/training-session")
    ResponseEntity<String> saveTrainingSession(@RequestBody TrainingSessionDto trainingSessionDto);
}
