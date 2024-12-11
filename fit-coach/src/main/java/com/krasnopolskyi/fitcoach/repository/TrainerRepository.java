package com.krasnopolskyi.fitcoach.repository;

import com.krasnopolskyi.fitcoach.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    @Query("SELECT t FROM Trainer t JOIN t.user u WHERE u.username = :username")
    Optional<Trainer> findByUsername(@Param("username") String username);
    @Query("SELECT t FROM Trainer t JOIN t.user u WHERE u.isActive = true")
    List<Trainer> findAllActiveTrainers();
}
