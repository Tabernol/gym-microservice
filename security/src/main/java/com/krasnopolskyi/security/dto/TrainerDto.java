package com.krasnopolskyi.security.dto;

import com.krasnopolskyi.security.utils.validation.Create;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
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
