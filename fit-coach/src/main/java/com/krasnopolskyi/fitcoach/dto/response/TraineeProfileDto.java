package com.krasnopolskyi.fitcoach.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TraineeProfileDto {
    private String username;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;
    private boolean isActive;
    private List<TrainerProfileShortDto> trainers;

}
