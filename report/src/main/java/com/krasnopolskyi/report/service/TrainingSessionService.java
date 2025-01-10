package com.krasnopolskyi.report.service;

import com.krasnopolskyi.report.entity.TrainingSession;
import com.krasnopolskyi.report.repository.TrainingSessionRepository;
import jakarta.jms.JMSException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingSessionService {
    private final TrainingSessionRepository trainingSessionRepository;

    @JmsListener(destination = "training.session")
    @Transactional("jmsTransactionManager")
    public void receiveTrainingSessionMessage(TrainingSession trainingSession) {
        log.info("Received message from training.session: {}", trainingSession);

        if (trainingSession.getDate() == null) {
            // Throw an exception to trigger retry and DLQ
            throw new IllegalArgumentException("Training session date cannot be null.");
        }

        try {
            trainingSessionRepository.save(trainingSession);
        } catch (Exception e) {
            log.error("Error processing training session message", e);
            throw new RuntimeException("Database save failed");
        }
    }
}
