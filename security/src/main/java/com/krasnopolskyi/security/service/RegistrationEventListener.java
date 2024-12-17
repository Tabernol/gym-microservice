//package com.krasnopolskyi.security.service;
//
//import com.krasnopolskyi.security.dto.TraineeFullDto;
//import com.krasnopolskyi.security.dto.event.RegisterTraineeEvent;
//import com.krasnopolskyi.security.http.client.FitCoachClient;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.event.EventListener;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//@Service
//@Slf4j
//public class RegistrationEventListener {
//
//    private final FitCoachClient fitCoachClient;
//
//    public RegistrationEventListener(FitCoachClient fitCoachClient) {
//        this.fitCoachClient = fitCoachClient;
//    }
//
//    @EventListener // or maybe should I use TransactionEventListener;
//    public void handleAddTrainingSessionEvent(RegisterTraineeEvent event) {
//        TraineeFullDto fullDto = event.getTraineeFullDto();
//        ResponseEntity<String> response = fitCoachClient.saveTrainee(fullDto);
//        log.info("Response from report microservice: " + response.getStatusCode());
//    }
//}
