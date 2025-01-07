package com.krasnopolskyi.report.service;

import com.krasnopolskyi.report.entity.TrainingSession;
import com.krasnopolskyi.report.repository.TrainingSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingSessionService {
    private final TrainingSessionRepository trainingSessionRepository;

    @JmsListener(destination = "training.session")
    public void receiveTrainingSessionMessage(TrainingSession trainingSession) {
        log.info("Received message from training.session: {}", trainingSession);

        try {
            // save to database
            trainingSessionRepository.save(trainingSession);
        } catch (Exception e) {
            log.error("Error processing user message", e);
        }
    }
}
