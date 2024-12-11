package com.krasnopolskyi.fitcoach.dto.request;

import com.krasnopolskyi.fitcoach.validation.Create;
import com.krasnopolskyi.fitcoach.validation.annotation.CustomValidAge;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TraineeUpdateDto(

        @NotBlank(groups = Create.class, message = "Username can't be null")
        @Size(groups = Create.class, min = 2, max = 64, message = "Username must be between 2 and 64 characters")
        String username,
        @NotBlank(groups = Create.class, message = "First name can't be null")
        @Size(groups = Create.class, min = 2, max = 32, message = "First name must be between 2 and 32 characters")
        String firstName,
        @NotBlank(groups = Create.class, message = "Last name can't be null")
        @Size(groups = Create.class, min = 2, max = 32, message = "Last name must be between 2 and 32 characters")
        String lastName,
        @CustomValidAge(groups = Create.class, message = "Date of birth must be valid")
        LocalDate dateOfBirth,
        @Size(groups = Create.class, max = 256, message = "Address max size is 256 characters")
        String address,
        @NotNull(groups = Create.class, message = "Status can't be null")
        Boolean isActive
) {
}
