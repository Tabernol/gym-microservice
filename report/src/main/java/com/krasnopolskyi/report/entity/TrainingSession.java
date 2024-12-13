package com.krasnopolskyi.report.entity;

import com.krasnopolskyi.report.dto.TrainingSessionOperation;
import jakarta.annotation.Generated;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
