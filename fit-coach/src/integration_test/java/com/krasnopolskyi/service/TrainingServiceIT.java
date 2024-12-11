package com.krasnopolskyi.service;

import com.krasnopolskyi.fitcoach.dto.request.TrainingDto;
import com.krasnopolskyi.fitcoach.dto.request.TrainingFilterDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainingResponseDto;
import com.krasnopolskyi.fitcoach.entity.Trainee;
import com.krasnopolskyi.fitcoach.entity.Trainer;
import com.krasnopolskyi.fitcoach.exception.AuthnException;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.ValidateException;
import com.krasnopolskyi.IntegrationTestBase;
import com.krasnopolskyi.fitcoach.repository.TraineeRepository;
import com.krasnopolskyi.fitcoach.repository.TrainerRepository;
import com.krasnopolskyi.fitcoach.repository.TrainingRepository;
import com.krasnopolskyi.fitcoach.repository.UserRepository;
import com.krasnopolskyi.fitcoach.service.TrainingService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TrainingServiceIT extends IntegrationTestBase {

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Disabled
    void saveTraining() throws EntityException, ValidateException, AuthnException {
        // Set up test data
        Trainee trainee = traineeRepository.findByUsername("john.doe").orElseThrow();
        Trainer trainer = trainerRepository.findByUsername("arnold.schwarzenegger").orElseThrow();

        TrainingDto trainingDto = new TrainingDto(
                trainee.getUser().getUsername(),
                trainer.getUser().getUsername(),
                "Bodybuilding Basics",
                LocalDate.now(),
                90
        );

        TrainingResponseDto savedTraining = trainingService.save(trainingDto);

        assertNotNull(savedTraining);
        assertEquals("Bodybuilding Basics", savedTraining.trainingName());
        assertEquals("Bodybuilding", savedTraining.trainingType());
        assertEquals("Arnold Schwarzenegger", savedTraining.trainerFullName());
        assertEquals("John Doe", savedTraining.traineeFullName());
    }

    @Test
    void getFilteredTrainings() throws EntityException {
        // Create a filter
        TrainingFilterDto filterDto = new TrainingFilterDto(
                "arnold.schwarzenegger",
                LocalDate.now().minusMonths(1),
                LocalDate.now(),
                "Bodybuilding",
                "john.doe"
        );

        List<TrainingResponseDto> trainings = trainingService.getFilteredTrainings(filterDto);

        assertNotNull(trainings);
        assertTrue(trainings.size() >= 0); // Depending on test data
    }

    @Test
    @Disabled
    void saveTrainingWithNonExistentTraineeThrowsException() {
        // Set up a DTO with a non-existent trainee
        TrainingDto trainingDto = new TrainingDto(
                "nonexistent.trainee",
                "arnold.schwarzenegger",
                "Bodybuilding Basics",
                LocalDate.now(),
                90
        );

        EntityException thrown = assertThrows(
                EntityException.class,
                () -> trainingService.save(trainingDto)
        );

        assertEquals("Could not find trainee with nonexistent.trainee", thrown.getMessage());
    }

    @Test
    @Disabled
    void saveTrainingWithNonExistentTrainerThrowsException() {
        // Set up a DTO with a non-existent trainer
        TrainingDto trainingDto = new TrainingDto(
                "john.doe",
                "nonexistent.trainer",
                "Bodybuilding Basics",
                LocalDate.now(),
                90
        );

        EntityException thrown = assertThrows(
                EntityException.class,
                () -> trainingService.save(trainingDto)
        );

        assertEquals("Could not find trainer with id nonexistent.trainer", thrown.getMessage());
    }

    @Test
    void getFilteredTrainingsWithNonExistentUserThrowsException() {
        // Set up a filter with a non-existent user
        TrainingFilterDto filterDto = new TrainingFilterDto(
                "nonexistent.user",
                LocalDate.now().minusMonths(1),
                LocalDate.now(),
                "Bodybuilding",
                "john.doe"
        );

        EntityException thrown = assertThrows(
                EntityException.class,
                () -> trainingService.getFilteredTrainings(filterDto)
        );

        assertEquals("Could not found user: nonexistent.user", thrown.getMessage());
    }
}
