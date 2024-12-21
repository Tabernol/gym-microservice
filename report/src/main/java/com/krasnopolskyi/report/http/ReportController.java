package com.krasnopolskyi.report.http;

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
@RequestMapping("/api/v1/report")
@Slf4j
public class ReportController {
    private final ReportService reportService;
    private final TrainingSessionService trainingSessionService;

    @PostMapping("/training-session")
    public ResponseEntity<String> addTrainingSession(@RequestBody TrainingSession trainingSession){
        log.info("call to service");
        TrainingSession result = trainingSessionService.saveTrainingSession(trainingSession);
        log.info("Saved: " + result);
        return ResponseEntity.ok().body("added");
    }

    @GetMapping
    public ResponseEntity<ReportTraining> getReportByUsername(@RequestParam("username") String username){
        log.info("controller call");
        return ResponseEntity.ok().body(reportService.getReportByUsername(username));
    }

}
