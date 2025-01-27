package com.krasnopolskyi.int_test.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingDto {
    private String traineeUsername;
    private String trainerUsername;
    private String trainingName;
    private LocalDate date;
    private Integer duration;
}
