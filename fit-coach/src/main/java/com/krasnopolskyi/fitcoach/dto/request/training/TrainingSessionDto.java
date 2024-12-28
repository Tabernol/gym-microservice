package com.krasnopolskyi.fitcoach.dto.request.training;

import java.time.LocalDate;

public record TrainingSessionDto(
        long id,
        String username,
        LocalDate date,
        int duration,
        TrainingSessionOperation operation) {
}
