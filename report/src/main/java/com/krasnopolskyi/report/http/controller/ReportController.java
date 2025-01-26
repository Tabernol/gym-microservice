package com.krasnopolskyi.report.http.controller;

import com.krasnopolskyi.report.entity.ReportTrainer;
import com.krasnopolskyi.report.service.ReportService;
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

    @GetMapping("/generate/{username}")
    public ResponseEntity<ReportTrainer> getReportByUsername(@PathVariable("username") String username){
        return ResponseEntity.ok().body(reportService.getReportByUsername(username));
    }

}
