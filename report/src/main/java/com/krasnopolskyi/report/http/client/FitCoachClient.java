package com.krasnopolskyi.report.http.client;

import com.krasnopolskyi.report.model.Trainer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "fit-coach", url = "http://localhost:8765/api/v1/fit-coach/trainers")
public interface FitCoachClient {

    // POST request to add a new training
    @GetMapping("/{username}")
    ResponseEntity<Trainer> getTrainer(@PathVariable("username") String username);
}
