package com.krasnopolskyi.fitcoach.service;

import com.krasnopolskyi.fitcoach.dto.request.trainee.TraineeDto;
import com.krasnopolskyi.fitcoach.dto.request.trainer.TrainerDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SaveUserListener {
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public SaveUserListener(TraineeService traineeService, TrainerService trainerService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    // Listener for Trainee Queue
    @JmsListener(destination = "trainee.queue")
    public void receiveTraineeMessage(TraineeDto traineeDto) {
        log.info("Received message from trainee.queue: {}", traineeDto);

        try {
            traineeService.save(traineeDto);
        } catch (Exception e) {
            log.error("Error processing trainee message", e);
        }
    }

    // Listener for Trainer Queue
    @JmsListener(destination = "trainer.queue")
    public void receiveTrainerMessage(TrainerDto trainerDto) {
        log.info("Received message from trainer.queue: {}", trainerDto);

        try {
            trainerService.save(trainerDto);
        } catch (Exception e) {
            log.error("Error processing trainer message", e);
        }
    }
}
