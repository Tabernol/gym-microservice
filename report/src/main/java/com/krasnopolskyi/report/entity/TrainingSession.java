package com.krasnopolskyi.report.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "training_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingSession {
    @Id
    private long id;
    private String username;
    private LocalDate date;
    private int duration;

    @Enumerated(EnumType.STRING)
    private TrainingSessionOperation operation;
}
