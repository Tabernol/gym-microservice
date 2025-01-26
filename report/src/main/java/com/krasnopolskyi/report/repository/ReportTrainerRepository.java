package com.krasnopolskyi.report.repository;

import com.krasnopolskyi.report.entity.ReportTrainer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ReportTrainerRepository extends MongoRepository<ReportTrainer, String> {
    Optional<ReportTrainer> findByUsername(String username);
}
