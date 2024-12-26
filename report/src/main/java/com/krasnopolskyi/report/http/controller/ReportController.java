package com.krasnopolskyi.report.http.controller;

import com.krasnopolskyi.report.entity.TrainingSession;
import com.krasnopolskyi.report.model.ReportTraining;
import com.krasnopolskyi.report.service.ReportService;
import com.krasnopolskyi.report.service.TrainingSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fit-coach/report")
@Slf4j
public class ReportController {
    private final ReportService reportService;
    private final TrainingSessionService trainingSessionService;

    @PostMapping("/training-session")
    public ResponseEntity<TrainingSession> addTrainingSession(@RequestBody TrainingSession trainingSession){
        log.info("try to save training session");
        TrainingSession result = trainingSessionService.saveTrainingSession(trainingSession);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/generate/{username}")
    public ResponseEntity<ReportTraining> getReportByUsername(@PathVariable("username") String username){
        log.info("GENERATE REPORT FOR TRAINER " + username);
        return ResponseEntity.ok().body(reportService.getReportByUsername(username));
    }

}
