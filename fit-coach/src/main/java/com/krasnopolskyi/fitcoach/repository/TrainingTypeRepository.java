package com.krasnopolskyi.fitcoach.repository;

import com.krasnopolskyi.fitcoach.entity.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainingTypeRepository extends JpaRepository<TrainingType, Integer> {
}
