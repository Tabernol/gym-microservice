package com.krasnopolskyi.fitcoach.service;

import com.krasnopolskyi.fitcoach.dto.request.training.TrainingDto;
import com.krasnopolskyi.fitcoach.dto.request.training.TrainingFilterDto;
import com.krasnopolskyi.fitcoach.dto.request.training.TrainingSessionDto;
import com.krasnopolskyi.fitcoach.dto.request.training.TrainingSessionOperation;
import com.krasnopolskyi.fitcoach.dto.response.TrainingResponseDto;
import com.krasnopolskyi.fitcoach.entity.*;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.GymException;
import com.krasnopolskyi.fitcoach.exception.ValidateException;
import com.krasnopolskyi.fitcoach.repository.TraineeRepository;
import com.krasnopolskyi.fitcoach.repository.TrainerRepository;
import com.krasnopolskyi.fitcoach.repository.TrainingRepository;
import com.krasnopolskyi.fitcoach.repository.UserRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {
    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JmsTemplate jmsTemplate;

    private SimpleMeterRegistry meterRegistry;

    private TrainingService trainingService;

    private Trainee mockTrainee;
    private User mockUser;

    private Trainer mockTrainer;
    private User mockUserTrainer;

    private TrainingDto trainingDto;

    private Training mockTraining;
    private TrainingSessionDto trainingSessionDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        meterRegistry = new SimpleMeterRegistry();
        trainingService = new TrainingService(trainingRepository, traineeRepository, trainerRepository, userRepository, meterRegistry, jmsTemplate);

        mockUser = new User();
        mockUser.setUsername("john.doe");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setIsActive(true);

        mockTrainee = new Trainee();
        mockTrainee.setUser(mockUser);
        mockTrainee.setAddress("123 Test St");

        mockUserTrainer = new User();
        mockUserTrainer.setUsername("trainer.doe");
        mockUserTrainer.setFirstName("Trainer");
        mockUserTrainer.setLastName("Doe");
        mockUserTrainer.setIsActive(true);

        mockTrainer = new Trainer();
        mockTrainer.setUser(mockUserTrainer);
        mockTrainer.setSpecialization(new TrainingType(1, "Cardio"));


        trainingDto = new TrainingDto();
        trainingDto.setTraineeUsername("john.doe");
        trainingDto.setTrainerUsername("trainer.doe");
        trainingDto.setDate(LocalDate.now());
        trainingDto.setDuration(60);
        trainingDto.setTrainingName("Strength Training");



        mockTraining = new Training();
        mockTraining.setId(12L);
        mockTraining.setTrainee(mockTrainee);
        mockTraining.setTrainer(mockTrainer);
        mockTraining.setTrainingType(mockTrainer.getSpecialization());
        mockTraining.setDate(trainingDto.getDate());
        mockTraining.setDuration(trainingDto.getDuration());
        mockTraining.setTrainingName(trainingDto.getTrainingName());

        trainingSessionDto = new TrainingSessionDto(12L,
                "trainer.doe",
                LocalDate.now(),
                60,
                TrainingSessionOperation.ADD);
    }


    @Test
    void testSaveSuccess() throws GymException {

        when(traineeRepository.findByUsername(trainingDto.getTraineeUsername())).thenReturn(Optional.of(mockTrainee));
        when(trainerRepository.findByUsername(trainingDto.getTrainerUsername())).thenReturn(Optional.of(mockTrainer));
        when(trainingRepository.save(any(Training.class))).thenReturn(mockTraining);


        TrainingResponseDto response = trainingService.save(trainingDto);

        assertNotNull(response);
        assertEquals("Strength Training", response.trainingName());
        verify(trainingRepository, times(1)).save(any(Training.class));
    }

    @Test
    void testSaveTraineeNotFound() {
        when(traineeRepository.findByUsername(trainingDto.getTraineeUsername())).thenReturn(Optional.empty());

        GymException exception = assertThrows(EntityException.class, () -> trainingService.save(trainingDto));
        assertEquals("Could not find trainee with " + trainingDto.getTraineeUsername(), exception.getMessage());
    }

    @Test
    void testSaveTrainerNotFound() {
        when(traineeRepository.findByUsername(trainingDto.getTraineeUsername())).thenReturn(Optional.of(mockTrainee));
        when(trainerRepository.findByUsername(trainingDto.getTrainerUsername())).thenReturn(Optional.empty());

        GymException exception = assertThrows(EntityException.class, () -> trainingService.save(trainingDto));
        assertEquals("Could not find trainer with id " + trainingDto.getTrainerUsername(), exception.getMessage());
    }

    @Test
    void testSaveUserInactive() {
        mockTrainer.getUser().setIsActive(false);

        when(traineeRepository.findByUsername(trainingDto.getTraineeUsername())).thenReturn(Optional.of(mockTrainee));
        when(trainerRepository.findByUsername(trainingDto.getTrainerUsername())).thenReturn(Optional.of(mockTrainer));

        ValidateException exception = assertThrows(ValidateException.class, () -> trainingService.save(trainingDto));
        assertEquals("Profile " + mockTrainer.getUser().getFirstName() + " " + mockTrainer.getUser().getLastName() + " is currently disabled", exception.getMessage());
    }


    @Test
    void getFilteredTrainings_shouldReturnListOfTrainingResponseDto_whenSuccessful() throws EntityException {
        // Arrange
        TrainingDto trainingDto = new TrainingDto(
                mockTrainee.getUser().getUsername(),
                mockTrainer.getUser().getUsername(),
                "Training1",
                LocalDate.now(),
                60);

        when(userRepository.findByUsername(mockTrainee.getUser().getUsername())).thenReturn(Optional.of(mockTrainee.getUser()));

        Training training = new Training();
        training.setTrainee(mockTrainee);
        training.setTrainer(mockTrainer);
        training.setDuration(60);
        training.setTrainingName("Training1");
        training.setDate(LocalDate.now());
        training.setTrainingType(mockTrainer.getSpecialization());

        TrainingFilterDto filter = new TrainingFilterDto();
        filter.setOwner(mockTrainee.getUser().getUsername());

        List<Training> trainings = List.of(training);
        when(trainingRepository.getFilteredTrainings(mockTrainee.getUser().getUsername(), null, null, null, null))
                .thenReturn(trainings);

        // Act
        List<TrainingResponseDto> responseList = trainingService.getFilteredTrainings(filter);

        // Assert
        assertEquals(1, responseList.size());
        assertEquals("Training1", responseList.get(0).trainingName());
        verify(userRepository, times(1)).findByUsername(mockTrainee.getUser().getUsername());
        verify(trainingRepository, times(1)).getFilteredTrainings(mockTrainee.getUser().getUsername(), null, null, null, null);
    }

    @Test
    void getFilteredTrainings_shouldThrowEntityException_whenOwnerNotFound() {
        // Arrange
        String ownerUsername = "owner";
        TrainingFilterDto filter = new TrainingFilterDto(ownerUsername, LocalDate.now().minusDays(10), LocalDate.now(), "Yoga", "partner");

        when(userRepository.findByUsername(ownerUsername)).thenReturn(Optional.empty());

        // Act & Assert
        EntityException exception = assertThrows(EntityException.class, () -> {
            trainingService.getFilteredTrainings(filter);
        });
        assertEquals("Could not found user: " + ownerUsername, exception.getMessage());
        verify(userRepository, times(1)).findByUsername(ownerUsername);
        verify(trainingRepository, never()).getFilteredTrainings(anyString(), anyString(), any(), any(), anyString());
    }
    @Test
    void deleteTraining_Success() throws GymException {
        when(trainingRepository.findById(12L)).thenReturn(Optional.ofNullable(mockTraining));

        TrainingSessionDto dto = new TrainingSessionDto(12L,
                "trainer.doe",
                LocalDate.now(),
                60,
                TrainingSessionOperation.DELETE);

        boolean result = trainingService.delete(12L);

        assertTrue(result);
        verify(trainingRepository, times(2)).findById(12L);
    }
}
