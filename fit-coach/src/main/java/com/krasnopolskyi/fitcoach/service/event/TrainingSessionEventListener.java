package com.krasnopolskyi.fitcoach.service.event;

import com.krasnopolskyi.fitcoach.dto.event.TrainingSessionEvent;
import com.krasnopolskyi.fitcoach.dto.request.training.TrainingSessionDto;
import com.krasnopolskyi.fitcoach.http.client.ReportClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

@Service
@Slf4j
public class TrainingSessionEventListener {

    private final ReportClient reportClient; // Feign client or RestTemplate to call the second microservice

    private final Queue<TrainingSessionEvent> events = new LinkedList<>();

    public TrainingSessionEventListener(ReportClient reportClient) {
        this.reportClient = reportClient;
    }

    @EventListener
    public void handleTrainingSessionEvent(TrainingSessionEvent event) {
        try {
            TrainingSessionDto trainingSessionDto = event.getTrainingSessionDto();
            ResponseEntity<String> response = reportClient.saveTrainingSession(trainingSessionDto);
            log.info("Response from report microservice: " + response.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to call report microservice", e);
            events.add(event);
            //todo add the logic if report-service unavailable
            // Optionally retry, send to a queue, etc.
        }
    }

    // Scheduled task that retries every minute to send failed events
    @Scheduled(fixedRate = 60000) // Runs every 60 seconds
    public void retryFailedEvents() {
        log.info("Retrying failed events...");
        Iterator<TrainingSessionEvent> iterator = events.iterator();
        while (iterator.hasNext()) {
            TrainingSessionEvent event = iterator.next();
            try {
                TrainingSessionDto trainingSessionDto = event.getTrainingSessionDto();
                ResponseEntity<String> response = reportClient.saveTrainingSession(trainingSessionDto);
                log.info("Retry successful for event: " + trainingSessionDto.username());
                iterator.remove();  // Remove from the queue if it succeeds
            } catch (Exception e) {
                log.error("Retry failed for event: " + event.getTrainingSessionDto().username(), e);
            }
        }
    }

}
