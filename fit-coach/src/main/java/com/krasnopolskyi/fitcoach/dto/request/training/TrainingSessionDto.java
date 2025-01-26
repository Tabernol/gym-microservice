package com.krasnopolskyi.fitcoach.dto.request.training;

import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingSessionDto {
    private long id;
    private String username;
    private String firstName;
    private String lastName;
    private boolean active;
    private LocalDate date;
    private int duration;
    private TrainingSessionOperation operation;
}
