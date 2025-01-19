package com.krasnopolskyi.report.service;

import com.krasnopolskyi.report.entity.TrainingSession;
import com.krasnopolskyi.report.model.Trainer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomEventListener {

    private final ReportService reportService;

    @JmsListener(destination = "training.session", containerFactory = "jmsListenerContainerFactory")
    @Transactional(transactionManager = "jmsTransactionManager")  // Use JMS transactions
    public void receiveTrainingSessionMessage(TrainingSession trainingSession) {
        log.debug("Received message from training.session: {}", trainingSession);

        if (trainingSession.getDate() == null) {
            log.error("Training session is invalid.");
            // Throw an exception to trigger retry and DLQ
            throw new IllegalArgumentException("Training session date cannot be null.");
        }

        reportService.saveOrUpdateReport(trainingSession);
    }

    @JmsListener(destination = "report.trainer.data.updated", containerFactory = "jmsListenerContainerFactory")
    @Transactional(transactionManager = "jmsTransactionManager")
    public void onUserUpdated(Trainer user) {
        log.debug("Received message update trainer own data: " + user);
        reportService.updateTrainer(user);
    }
}
