package com.krasnopolskyi.fitcoach.service;

import com.krasnopolskyi.fitcoach.dto.request.trainee.TraineeDto;
import com.krasnopolskyi.fitcoach.dto.request.trainer.TrainerDto;
import com.krasnopolskyi.fitcoach.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserActionListener {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final UserService userService;

    @JmsListener(destination = "trainee.queue")
    public void receiveTraineeMessage(TraineeDto traineeDto) {
        log.info("Received message from trainee.queue: {}", traineeDto);

        try {
            traineeService.save(traineeDto);
        } catch (Exception e) {
            log.error("Error processing trainee message", e);
        }
    }

    @JmsListener(destination = "trainer.queue")
    public void receiveTrainerMessage(TrainerDto trainerDto) {
        log.info("Received message from trainer.queue: {}", trainerDto);

        try {
            trainerService.save(trainerDto);
        } catch (Exception e) {
            log.error("Error processing trainer message", e);
        }
    }

    @JmsListener(destination = "change.status.queue")
    public void receiveChangeStatusMessage(User user) {
        log.info("Received message from change.status.queue: {}", user);

        try {
            userService.updateLocalUser(user);
        } catch (Exception e) {
            log.error("Error processing user message", e);
        }
    }
}
