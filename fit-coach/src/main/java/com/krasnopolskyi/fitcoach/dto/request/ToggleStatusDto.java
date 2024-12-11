package com.krasnopolskyi.fitcoach.dto.request;

import com.krasnopolskyi.fitcoach.validation.Create;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ToggleStatusDto (
        @NotBlank(groups = Create.class, message = "Username can't be null")
        @Size(groups = Create.class, min = 2, max = 64, message = "Username must be between 2 and 64 characters")
        String username,
        @NotNull(groups = Create.class, message = "Status can't be null")
        Boolean isActive) {
}
