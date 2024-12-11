package com.krasnopolskyi.fitcoach.dto.request;

import com.krasnopolskyi.fitcoach.validation.Create;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordDto(
        @NotBlank(groups = Create.class, message = "Username can't be null")
        @Size(groups = Create.class, min = 2, max = 64, message = "Username must be between 2 and 64 characters")
        String username,
        @NotBlank(groups = Create.class, message = "Username can't be null")
        String oldPassword,
        @NotBlank(groups = Create.class, message = "Username can't be null")
        @Size(min = 4, max = 256, message = "Password must contain at least 4 and less than 256 characters")
        String newPassword) {
}
