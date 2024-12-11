package com.krasnopolskyi.fitcoach.repository;

import com.krasnopolskyi.fitcoach.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {
    @Query("SELECT t FROM Training t " +
            "JOIN t.trainee tr JOIN tr.user tru " +
            "JOIN t.trainer tn JOIN tn.user tnu " +
            "WHERE (:owner IS NULL OR tru.username = :owner OR tnu.username = :owner) " +
            "AND (:partner IS NULL OR tru.username = :partner OR tnu.username = :partner) " +
            "AND (:startDate IS NULL OR t.date >= :startDate) " +
            "AND (:endDate IS NULL OR t.date <= :endDate) " +
            "AND (:trainingType IS NULL OR t.trainingType.type = :trainingType)")
    List<Training> getFilteredTrainings(
            @Param("owner") String owner,
            @Param("partner") String partner,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("trainingType") String trainingType
    );
}
