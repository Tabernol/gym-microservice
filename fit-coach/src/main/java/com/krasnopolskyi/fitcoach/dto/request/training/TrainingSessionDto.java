package com.krasnopolskyi.fitcoach.dto.request.training;

import java.time.LocalDate;

public record TrainingSessionDto(
        long id,
        String username,
        String firstName,
        String lastName,
        boolean isActive,
        LocalDate date,
        int duration,
        TrainingSessionOperation operation) {
}
