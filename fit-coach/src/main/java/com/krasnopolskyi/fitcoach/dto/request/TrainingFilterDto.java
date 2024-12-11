package com.krasnopolskyi.fitcoach.dto.request;

import com.krasnopolskyi.fitcoach.validation.Create;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingFilterDto {
    @NotBlank(groups = Create.class, message = "Fill in username for search  by")
    private String owner; // username. owner of training
    private LocalDate startDate;
    private LocalDate endDate;
    private String trainingType;
    private String partner; // name. partner of training
}
