package com.krasnopolskyi.report.dto;

import java.time.LocalDate;

public record TrainingSessionDto(
        String username,
        String firstName,
        String lastName,
        boolean isActive,
        LocalDate date,
        int duration,
        TrainingSessionOperation operation
        ) {
}
