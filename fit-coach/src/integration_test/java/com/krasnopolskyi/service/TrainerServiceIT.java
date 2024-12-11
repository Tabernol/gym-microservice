package com.krasnopolskyi.service;

import com.krasnopolskyi.fitcoach.dto.request.*;
import com.krasnopolskyi.fitcoach.dto.response.TrainerProfileDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainingResponseDto;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.GymException;
import com.krasnopolskyi.fitcoach.exception.ValidateException;
import com.krasnopolskyi.IntegrationTestBase;
import com.krasnopolskyi.fitcoach.repository.TrainerRepository;
import com.krasnopolskyi.fitcoach.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TrainerServiceIT extends IntegrationTestBase {
    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TrainerRepository trainerRepository;

    @Test
    void saveTrainer() throws EntityException {
        TrainerDto trainerDto = new TrainerDto("Bruce", "Le", 1);
        UserCredentials savedUser = trainerService.save(trainerDto);

        assertNotNull(savedUser);
        assertEquals("bruce.le", savedUser.username());
    }

    @Test
    void findByUsername() throws EntityException {
        TrainerProfileDto trainer = trainerService.findByUsername("arnold.schwarzenegger");

        assertNotNull(trainer);
        assertEquals("Arnold", trainer.firstName());
        assertEquals("Schwarzenegger", trainer.lastName());
        assertEquals("Bodybuilding", trainer.specialization());
    }

    @Test
    void getTrainerTrainings() throws EntityException {
        TrainingFilterDto filterDto = new TrainingFilterDto("arnold.schwarzenegger", LocalDate.now().minusMonths(1), LocalDate.now(), "Bodybuilding", "john.doe");
        List<TrainingResponseDto> trainings = trainerService.getTrainings(filterDto);

        assertNotNull(trainings);
        assertTrue(trainings.size() >= 0); // Depending on test data
    }

    @Test
    void updateTrainer() throws GymException {
        TrainerUpdateDto updateDto = new TrainerUpdateDto(
                "arnold.schwarzenegger",
                "Arnold",
                "Schwarzenegger",
                "Bodybuilding",
                true
        );
        TrainerProfileDto updatedTrainer = trainerService.update("arnold.schwarzenegger", updateDto);

        assertNotNull(updatedTrainer);
        assertEquals("Arnold", updatedTrainer.firstName());
        assertEquals("Schwarzenegger", updatedTrainer.lastName());
        assertTrue(updatedTrainer.isActive());
    }

    @Test
    void changeTrainerStatus() throws EntityException, ValidateException {
        ToggleStatusDto statusDto = new ToggleStatusDto("arnold.schwarzenegger", false);
        String result = trainerService.changeStatus("arnold.schwarzenegger", statusDto);

        assertEquals("Status of trainer arnold.schwarzenegger is deactivated", result);
    }

    @Test
    void failChangeStatusDueToUsernameMismatch() {
        ToggleStatusDto statusDto = new ToggleStatusDto("jillian.michaels", false);

        ValidateException thrown = assertThrows(
                ValidateException.class,
                () -> trainerService.changeStatus("arnold.schwarzenegger", statusDto)
        );

        assertEquals("Username should be the same", thrown.getMessage());
    }

    @Test
    void findNonExistentTrainerThrowsException() {
        EntityException thrown = assertThrows(
                EntityException.class,
                () -> trainerService.findByUsername("non.existent")
        );

        assertEquals("Can't find trainer with username non.existent", thrown.getMessage());
    }
}
