package com.krasnopolskyi.report.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "training_session")
@Getter
@Setter
@NoArgsConstructor
public class TrainingSession {
    @Id
    private long id;
    private String username;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private LocalDate date;
    private int duration;

    @Enumerated(EnumType.STRING)
    private TrainingSessionOperation operation;
}
