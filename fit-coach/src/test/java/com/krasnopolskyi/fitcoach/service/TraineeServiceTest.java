package com.krasnopolskyi.fitcoach.service;

import com.krasnopolskyi.fitcoach.dto.request.*;
import com.krasnopolskyi.fitcoach.dto.response.TraineeProfileDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainerProfileShortDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainingResponseDto;
import com.krasnopolskyi.fitcoach.dto.response.UserDto;
import com.krasnopolskyi.fitcoach.entity.Trainee;
import com.krasnopolskyi.fitcoach.entity.Trainer;
import com.krasnopolskyi.fitcoach.entity.TrainingType;
import com.krasnopolskyi.fitcoach.entity.User;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.ValidateException;
import com.krasnopolskyi.fitcoach.repository.TraineeRepository;
import com.krasnopolskyi.fitcoach.repository.TrainerRepository;
import com.krasnopolskyi.fitcoach.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {
    @InjectMocks
    private TraineeService traineeService;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserServiceImpl userServiceImpl;

    @Mock
    private TrainingService trainingService;

    private Trainee mockTrainee;
    private User mockUser;

    private Trainer mockTrainer;
    private User mockUserTrainer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        traineeService = new TraineeService(traineeRepository, trainerRepository, userServiceImpl, trainingService);
        // Mock User and Trainee
        mockUser = new User();
        mockUser.setUsername("john.doe");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setPassword("password123");
        mockUser.setIsActive(true);

        mockTrainee = new Trainee();
        mockTrainee.setUser(mockUser);
        mockTrainee.setAddress("123 Test St");

        mockUserTrainer = new User();
        mockUserTrainer.setUsername("trainer.doe");
        mockUserTrainer.setFirstName("Trainer");
        mockUserTrainer.setLastName("Doe");
        mockUserTrainer.setPassword("password123");
        mockUserTrainer.setIsActive(true);

        mockTrainer = new Trainer();
        mockTrainer.setUser(mockUserTrainer);
        mockTrainer.setSpecialization(new TrainingType(1,"Cardio"));

    }

    @Test
    void testSaveTraineeSuccess() {
        TraineeDto traineeDto = new TraineeDto("John", "Doe", LocalDate.of(1990, 1, 1), "123 Test St");

        when(userServiceImpl.create(any(UserDto.class))).thenReturn(mockUser);
        when(traineeRepository.save(any(Trainee.class))).thenReturn(mockTrainee);

        UserCredentials result = traineeService.save(traineeDto);

        assertEquals(mockUser.getUsername(), result.username());
        assertNotNull(result.password());
        verify(traineeRepository, times(1)).save(any(Trainee.class));
    }

    @Test
    void testFindByUsernameSuccess() throws EntityException {
        mockTrainee.getTrainers().add(mockTrainer);
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(mockTrainee));

        TraineeProfileDto result = traineeService.findByUsername("john.doe");

        assertEquals(mockUser.getFirstName(), result.getFirstName());
        assertEquals(mockUser.getLastName(), result.getLastName());
    }

    @Test
    void testFindByUsernameThrowsException() {
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.empty());

        assertThrows(EntityException.class, () -> traineeService.findByUsername("john.doe"));
    }

    @Test
    void testUpdateTraineeSuccess() throws EntityException, ValidateException {
        TraineeUpdateDto traineeUpdateDto = new TraineeUpdateDto("john.doe", "John", "Doe", LocalDate.of(1990, 1, 1), "456 New St", true);

        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(mockTrainee));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(mockTrainee);

        TraineeProfileDto result = traineeService.update("john.doe", traineeUpdateDto);

        assertEquals(traineeUpdateDto.address(), result.getAddress());
        verify(traineeRepository, times(1)).save(any(Trainee.class));
    }

    @Test
    void testUpdateTraineeFailed() throws EntityException, ValidateException {
        TraineeUpdateDto traineeUpdateDto = new TraineeUpdateDto("john.doe", "John", "Doe", LocalDate.of(1990, 1, 1), "456 New St", true);

        assertThrows(ValidateException.class,
                () -> {traineeService.update("another.doe", traineeUpdateDto);});
    }

    @Test
    void testDeleteTraineeSuccess() throws EntityException {
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(mockTrainee));

        boolean result = traineeService.delete("john.doe");

        assertTrue(result);
        verify(traineeRepository, times(1)).delete(mockTrainee);
    }

    @Test
    void testDeleteTraineeFail() throws EntityException {
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.empty());

        boolean result = traineeService.delete("john.doe");

        assertFalse(result);
    }

    @Test
    void testUpdateTrainersSuccess() throws EntityException {
        List<String> trainerUsernames = List.of("trainer1");

        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(mockTrainee));
        when(trainerRepository.findByUsername(anyString())).thenReturn(Optional.of(mockTrainer));

        List<TrainerProfileShortDto> result = traineeService.updateTrainers("john.doe", trainerUsernames);

        assertEquals(1, result.size());
        verify(trainerRepository, times(1)).findByUsername(anyString());
    }

    @Test
    void testFindAllNotAssignedTrainersByTrainee() throws EntityException {
        Trainer trainer = new Trainer();
        List<Trainer> allTrainers = new ArrayList<>();
        allTrainers.add(mockTrainer);
        allTrainers.add(trainer);

        mockTrainee.getTrainers().add(trainer);

        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(mockTrainee));
        when(trainerRepository.findAllActiveTrainers()).thenReturn(allTrainers);

        List<TrainerProfileShortDto> result = traineeService.findAllNotAssignedTrainersByTrainee("john.doe");

        assertEquals(1, result.size());
        assertEquals(mockTrainer, allTrainers.get(0));
    }

    @Test
    void testChangeStatusSuccess() throws EntityException, ValidateException {
        ToggleStatusDto statusDto = new ToggleStatusDto("john.doe", true);

        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(mockTrainee));
        when(userServiceImpl.changeActivityStatus(any(ToggleStatusDto.class))).thenReturn(mockUser);

        String result = traineeService.changeStatus("john.doe", statusDto);

        assertEquals("Status of trainee john.doe is activated", result);
    }

    @Test
    void testChangeStatusThrowException() throws EntityException, ValidateException {
        ToggleStatusDto statusDto = new ToggleStatusDto("another.doe", true);

        assertThrows(ValidateException.class, () ->
                traineeService.changeStatus("john.doe", statusDto));
    }

    @Test
    public void testGetTrainings_Success() throws EntityException {
        // Arrange
        TrainingFilterDto filter = new TrainingFilterDto("john.doe", null, null, null, null);
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.ofNullable(mockTrainee));
        List<TrainingResponseDto> trainings = List.of(new TrainingResponseDto(1L, "Training 1", "Strength", "Trainer Doe", "John Doe", LocalDate.now(), 60));
        when(trainingService.getFilteredTrainings(filter)).thenReturn(trainings);

        // Act
        List<TrainingResponseDto> result = traineeService.getTrainings(filter);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(traineeRepository).findByUsername("john.doe");
        verify(trainingService).getFilteredTrainings(filter);
    }

}
