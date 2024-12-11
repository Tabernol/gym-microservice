package com.krasnopolskyi.fitcoach.dto.response;

import java.time.LocalDate;

public record TrainingResponseDto(
        Long id,
        String trainingName,
        String trainingType,
        String trainerFullName,
        String traineeFullName,
        LocalDate date,
        Integer duration) {
}
