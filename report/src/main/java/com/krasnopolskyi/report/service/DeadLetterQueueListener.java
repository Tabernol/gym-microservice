package com.krasnopolskyi.report.service;

import com.krasnopolskyi.report.entity.TrainingSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeadLetterQueueListener {

    @JmsListener(destination = "${spring.activemq.listener.queues.dead-letter-queue}",
            containerFactory = "jmsListenerContainerFactory")
    public void processDeadLetterQueue(TrainingSession failedTrainingSession) {
        // Handle or log the invalid message
        log.info("Received message in DLQ: " + failedTrainingSession);
    }
}
