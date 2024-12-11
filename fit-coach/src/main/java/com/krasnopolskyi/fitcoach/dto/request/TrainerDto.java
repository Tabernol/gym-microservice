package com.krasnopolskyi.fitcoach.dto.request;

import com.krasnopolskyi.fitcoach.validation.Create;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDto {
    @NotBlank(groups = Create.class, message = "First name can't be null")
    @Size(groups = Create.class, min = 2, max = 32, message = "First name must be between 2 and 32 characters")
    private String firstName;

    @NotBlank(groups = Create.class, message = "Last name can't be null")
    @Size(groups = Create.class, min = 2, max = 32, message = "Last name must be between 2 and 32 characters")
    private String lastName;

    @NotNull(groups = Create.class, message = "Specialization cannot be null")
    private Integer specialization;
}
