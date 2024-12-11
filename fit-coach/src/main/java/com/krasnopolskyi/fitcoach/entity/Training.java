package com.krasnopolskyi.fitcoach.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "training")
@Getter
@Setter
@NoArgsConstructor
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trainingName;

    @Column(name = "training_date")
    private LocalDate date;

    @Column(name = "training_duration")
    private Integer duration;

    @ManyToOne(fetch = FetchType.LAZY)
    private Trainee trainee;

    @ManyToOne(fetch = FetchType.LAZY)
    private Trainer trainer;

    @OneToOne
    @JoinColumn(name = "training_type_id")
    private TrainingType trainingType;
}
