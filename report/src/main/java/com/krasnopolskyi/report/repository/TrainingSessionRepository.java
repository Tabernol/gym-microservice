package com.krasnopolskyi.report.repository;

import com.krasnopolskyi.report.entity.TrainingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {

    List<TrainingSession> findAllByUsername(String username);
}
