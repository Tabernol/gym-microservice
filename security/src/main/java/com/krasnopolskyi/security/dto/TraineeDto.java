package com.krasnopolskyi.security.dto;

import com.krasnopolskyi.security.utils.validation.Create;
import com.krasnopolskyi.security.utils.validation.annotation.CustomValidAge;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class TraineeDto {
    @NotBlank(groups = Create.class, message = "First name can't be null")
    @Size(groups = Create.class, min = 2, max = 32, message = "First name must be between 2 and 32 characters")
    private String firstName;

    @NotBlank(groups = Create.class, message = "Last name can't be null")
    @Size(groups = Create.class, min = 2, max = 32, message = "Last name must be between 2 and 32 characters")
    private String lastName;

    @CustomValidAge(groups = Create.class, message = "Date of birth must be valid")
    private LocalDate dateOfBirth;

    @Size(groups = Create.class, max = 256, message = "Address must be less than 256 characters")
    private String address;
}
