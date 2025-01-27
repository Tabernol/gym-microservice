package com.krasnopolskyi.int_test.dto;

public record TrainerUpdateDto(
        String username,
        String firstName,
        String lastName,
        String specialization,
        Boolean isActive
) {
}
