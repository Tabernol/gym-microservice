package com.krasnopolskyi.fitcoach.dto.request.trainee;

import com.krasnopolskyi.fitcoach.validation.Create;
import com.krasnopolskyi.fitcoach.validation.annotation.CustomValidAge;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraineeFullDto {
    private long userId;
    private String firstName;
    private String lastName;
    private String username;
    private Boolean isActive;

    @CustomValidAge(groups = Create.class, message = "Date of birth must be valid")
    private LocalDate dateOfBirth;

    @Size(groups = Create.class, max = 256, message = "Address must be less than 256 characters")
    private String address;
}
