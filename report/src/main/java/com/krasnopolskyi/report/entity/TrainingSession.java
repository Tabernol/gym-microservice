package com.krasnopolskyi.report.entity;

import lombok.*;

import java.time.LocalDate;


@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingSession {
    private long id;
    private String username;
    private String firstName;
    private String lastName;
    private boolean active;
    private LocalDate date;
    private int duration;
    private TrainingSessionOperation operation;
}
