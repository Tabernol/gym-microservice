package com.krasnopolskyi.fitcoach.service;

import com.krasnopolskyi.fitcoach.dto.request.trainer.TrainerDto;
import com.krasnopolskyi.fitcoach.dto.request.trainer.TrainerUpdateDto;
import com.krasnopolskyi.fitcoach.dto.request.training.TrainingFilterDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainerProfileDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainingResponseDto;
import com.krasnopolskyi.fitcoach.entity.Trainee;
import com.krasnopolskyi.fitcoach.entity.Trainer;
import com.krasnopolskyi.fitcoach.entity.TrainingType;
import com.krasnopolskyi.fitcoach.entity.User;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.GymException;
import com.krasnopolskyi.fitcoach.exception.ValidateException;
import com.krasnopolskyi.fitcoach.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {
    @InjectMocks
    private TrainerService trainerService;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeService trainingTypeService;

    @Mock
    private TrainingService trainingService;

    private Trainee mockTrainee;
    private User mockUser;

    private Trainer mockTrainer;
    private User mockUserTrainer;

    @BeforeEach
    public void setUp() {
        trainerService = new TrainerService(trainerRepository, trainingTypeService, trainingService);

        // Mock User and Trainee
        mockUser = new User();
        mockUser.setUsername("john.doe");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setIsActive(true);

        mockTrainee = new Trainee();
        mockTrainee.setUser(mockUser);
        mockTrainee.setAddress("123 Test St");

        mockUserTrainer = new User();
        mockUserTrainer.setId(10L);
        mockUserTrainer.setUsername("trainer.doe");
        mockUserTrainer.setFirstName("Trainer");
        mockUserTrainer.setLastName("Doe");
        mockUserTrainer.setIsActive(true);

        mockTrainer = new Trainer();
        mockTrainer.setUser(mockUserTrainer);
        mockTrainer.setSpecialization(new TrainingType(1, "Cardio"));
    }


    @Test
    public void testSaveTrainer_Success() throws EntityException {
        // Arrange
        TrainerDto trainerDto = new TrainerDto(10,
                "Trainer",
                "Doe",
                "trainer.doe",
                true,
                1); // Sample trainer data
        TrainingType trainingType = new TrainingType(1, "Strength");


        when(trainingTypeService.findById(1)).thenReturn(trainingType);
        when(trainerRepository.save(any(Trainer.class))).thenReturn(mockTrainer);

        // Act
        Trainer save = trainerService.save(trainerDto);

        // Assert
        assertNotNull(save);
        assertEquals("trainer.doe", save.getUser().getUsername());
        verify(trainerRepository).save(any(Trainer.class));
        verify(trainingTypeService).findById(1);
    }

    @Test
    @DisplayName("Find trainer by username successfully")
    public void testFindByUsername_Success() throws EntityException {
        // Arrange
        String username = "trainer.doe";

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(mockTrainer));

        // Act
        TrainerProfileDto trainerProfile = trainerService.findByUsername(username);

        // Assert
        assertNotNull(trainerProfile);
        assertEquals(username, trainerProfile.username());
        verify(trainerRepository).findByUsername(username);
    }

    @Test
    @DisplayName("Get filtered trainings for trainer")
    public void testGetTrainings_Success() throws EntityException {
        // Arrange
        TrainingFilterDto filter = new TrainingFilterDto("john.doe", null, null, null, null);
        when(trainerRepository.findByUsername("john.doe")).thenReturn(Optional.of(new Trainer()));
        List<TrainingResponseDto> trainings = List.of(new TrainingResponseDto(1L, "Training 1", "Strength", "John Doe", "Trainee", LocalDate.now(), 60));
        when(trainingService.getFilteredTrainings(filter)).thenReturn(trainings);

        // Act
        List<TrainingResponseDto> result = trainerService.getTrainings(filter);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(trainerRepository).findByUsername("john.doe");
        verify(trainingService).getFilteredTrainings(filter);
    }

    @Test
    @DisplayName("Update trainer information successfully")
    public void testUpdateTrainer_Success() throws GymException {
        // Arrange
        TrainerUpdateDto trainerDto = new TrainerUpdateDto("trainer.doe", "new", "Doe", "Strength", true);

        when(trainerRepository.findByUsername("trainer.doe")).thenReturn(Optional.of(mockTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(mockTrainer);

        // Act
        TrainerProfileDto updatedProfile = trainerService.update("trainer.doe", trainerDto);

        // Assert
        assertNotNull(updatedProfile);
        verify(trainerRepository).findByUsername("trainer.doe");
        verify(trainerRepository).save(mockTrainer);
    }

    @Test
    public void testUpdateTrainer_Failed() throws GymException {
        // Arrange
        TrainerUpdateDto trainerDto = new TrainerUpdateDto("trainer.doe", "new", "Doe", "Strength", true);

        assertThrows(ValidateException.class,
                () -> trainerService.update("another.doe", trainerDto));

    }


//    @Test
//    @DisplayName("Change trainer status successfully")
//    public void testChangeStatus_Success() throws EntityException, ValidateException {
//        // Arrange
//        ToggleStatusDto statusDto = new ToggleStatusDto("trainer.doe", false);
//
//
//        when(trainerRepository.findByUsername("trainer.doe")).thenReturn(Optional.of(mockTrainer));
//        mockUserTrainer.setIsActive(false);
//        when(userServiceImpl.changeActivityStatus(statusDto)).thenReturn(mockUserTrainer);
//
//        // Act
//        String result = trainerService.changeStatus("trainer.doe", statusDto);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.contains("deactivated"));
//        verify(trainerRepository).findByUsername("trainer.doe");
//        verify(userServiceImpl).changeActivityStatus(statusDto);
//    }

    @Test
    @DisplayName("Throw exception when trainer not found by username")
    public void testFindByUsername_TrainerNotFound() {
        // Arrange
        String username = "john.doe";
        when(trainerRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityException.class, () -> trainerService.findByUsername(username));
        verify(trainerRepository).findByUsername(username);
    }

//    @Test
//    void testChangeStatusThrowException() throws EntityException, ValidateException {
//        ToggleStatusDto statusDto = new ToggleStatusDto("another.doe", true);
//
//        assertThrows(ValidateException.class, () ->
//                trainerService.changeStatus("john.doe", statusDto));
//    }
}
