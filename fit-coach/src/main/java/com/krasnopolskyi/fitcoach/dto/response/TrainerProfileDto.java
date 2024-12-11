package com.krasnopolskyi.fitcoach.dto.response;

import java.util.List;

public record TrainerProfileDto(String firstName,
                                String lastName,
                                String username,
                                String specialization,
                                boolean isActive,
                                List<UserProfileDto> trainees) {
}
