package com.krasnopolskyi.fitcoach.dto.request;

import com.krasnopolskyi.fitcoach.validation.Create;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingDto {

    @NotBlank(groups = Create.class, message = "Fill in trainee username")
    private String traineeUsername;

    @NotBlank(groups = Create.class, message = "Fill in trainer username")
    private String trainerUsername;

    @NotBlank(groups = Create.class, message = "Fill in training name")
    @Size(groups = Create.class, min = 2, max = 64, message = "Training name must be between 2 and 64 characters")
    private String trainingName;

    @PastOrPresent(groups = Create.class, message = "The training date cannot be in the future")
    private LocalDate date;

    @NotNull(groups = Create.class, message = "Duration cannot be null")
    @Min(groups = Create.class, value = 1, message = "The minimum duration is 1 minute")
    private Integer duration;
}
