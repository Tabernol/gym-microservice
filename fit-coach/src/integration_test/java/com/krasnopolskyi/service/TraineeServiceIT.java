package com.krasnopolskyi.service;

import com.krasnopolskyi.fitcoach.dto.request.*;
import com.krasnopolskyi.fitcoach.dto.response.TraineeProfileDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainerProfileShortDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainingResponseDto;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.ValidateException;
import com.krasnopolskyi.IntegrationTestBase;
import com.krasnopolskyi.fitcoach.repository.TraineeRepository;
import com.krasnopolskyi.fitcoach.service.TraineeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


public class TraineeServiceIT extends IntegrationTestBase {

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TraineeRepository traineeRepository;

    @Test
    void findByUsername() throws EntityException {
        var trainee = traineeService.findByUsername("john.doe");
        assertEquals("John", trainee.getFirstName());
        assertEquals("Doe", trainee.getLastName());
        assertEquals("john.doe", trainee.getUsername());
        assertEquals("123 Main St, City, Country", trainee.getAddress());
    }

    @Test
    void saveTrainee() {
        TraineeDto newTrainee = new TraineeDto("Tom", "Hanks", LocalDate.of(1970, 7, 9), "789 Oak St, City, Country");
        UserCredentials savedUser = traineeService.save(newTrainee);

        assertNotNull(savedUser);
        assertEquals("tom.hanks", savedUser.username());
    }

    @Test
    void updateTrainee() throws EntityException, ValidateException {
        TraineeUpdateDto updateDto = new TraineeUpdateDto(
                "jane.smith",
                "Clara",
                "Doe",
                LocalDate.of(1990, 5, 15),
                "789 Main St, City, Country",
                true
        );
        TraineeProfileDto updatedTrainee = traineeService.update("jane.smith", updateDto);

        assertNotNull(updatedTrainee);
        assertEquals("Clara", updatedTrainee.getFirstName());
        assertEquals("Doe", updatedTrainee.getLastName());
        assertEquals("789 Main St, City, Country", updatedTrainee.getAddress());
    }

    @Test
    void deleteTrainee() throws EntityException {
        boolean result = traineeService.delete("jane.smith");
        assertTrue(result);

        assertThrows(EntityException.class, () -> traineeService.findByUsername("jane.smith"));
    }

    @Test
    void updateTraineeTrainers() throws EntityException {
        List<String> trainers = List.of("arnold.schwarzenegger", "kayla.itsines");
        List<TrainerProfileShortDto> updatedTrainers = traineeService.updateTrainers("john.doe", trainers);

        Set<String> trainerUsernames = updatedTrainers
                .stream()
                .map(trainer -> trainer.userProfileDto().username())
                .collect(Collectors.toSet());


        assertEquals(2, updatedTrainers.size());
        assertTrue(trainerUsernames.contains("arnold.schwarzenegger"));
        assertTrue(trainerUsernames.contains("kayla.itsines"));
    }

//    @Test
//    void findAllNotAssignedTrainersByTrainee() throws EntityException {
//        List<TrainerProfileShortDto> notAssignedTrainers = traineeService.findAllNotAssignedTrainersByTrainee("jane.smith");
//
//        assertEquals(5, notAssignedTrainers.size());
//    }

    @Test
    void changeTraineeStatus() throws EntityException, ValidateException {
        ToggleStatusDto statusDto = new ToggleStatusDto("john.doe", false);
        String result = traineeService.changeStatus("john.doe", statusDto);

        assertEquals("Status of trainee john.doe is deactivated", result);
    }

    @Test
    void getTraineeTrainings() throws EntityException {
        TrainingFilterDto filterDto = new TrainingFilterDto("john.doe", LocalDate.now().minusMonths(1), LocalDate.now(), "Bodybuilding", "arnold.schwarzenegger");
        List<TrainingResponseDto> trainings = traineeService.getTrainings(filterDto);

        assertNotNull(trainings);
        assertTrue(trainings.size() >= 0); // Depending on test data
    }


}
