package com.krasnopolskyi.report.repository;

import com.krasnopolskyi.report.entity.TrainingSession;
import com.krasnopolskyi.report.entity.TrainingSessionOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
    List<TrainingSession> findAllByUsernameAndOperation(String username, TrainingSessionOperation operation);
}
