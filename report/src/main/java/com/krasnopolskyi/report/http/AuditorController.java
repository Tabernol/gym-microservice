package com.krasnopolskyi.report.http;

import com.krasnopolskyi.report.dto.TrainingSessionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/report")
@Slf4j
public class AuditorController {

    @PostMapping("/training-session")
    public ResponseEntity<String> addTrainingSession(@RequestBody TrainingSessionDto trainingSession){
        log.info("call to service");
        log.info(trainingSession.toString());
        return ResponseEntity.ok().body("added");
    }

    @GetMapping("/test")
    public String test(){
        return "test in client";
    }
}
