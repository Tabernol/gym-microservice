package com.krasnopolskyi.fitcoach.service;

import com.krasnopolskyi.fitcoach.entity.TrainingType;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.repository.TrainingTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceTest {
    @Mock
    private TrainingTypeRepository trainingTypeRepo;

    @InjectMocks
    private TrainingTypeService trainingTypeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trainingTypeService = new TrainingTypeService(trainingTypeRepo);
    }

    @Test
    void findById_shouldReturnTrainingType_whenIdExists() throws EntityException {
        // Arrange
        Integer trainingTypeId = 1;
        TrainingType expectedTrainingType = new TrainingType(trainingTypeId, "Yoga");
        when(trainingTypeRepo.findById(trainingTypeId)).thenReturn(Optional.of(expectedTrainingType));

        // Act
        TrainingType actualTrainingType = trainingTypeService.findById(trainingTypeId);

        // Assert
        assertEquals(expectedTrainingType, actualTrainingType);
        verify(trainingTypeRepo, times(1)).findById(trainingTypeId);
    }

    @Test
    void findById_shouldThrowEntityException_whenIdDoesNotExist() {
        // Arrange
        Integer trainingTypeId = 1;
        when(trainingTypeRepo.findById(trainingTypeId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityException exception = assertThrows(EntityException.class, () -> {
            trainingTypeService.findById(trainingTypeId);
        });
        assertEquals("Could not find training type with id " + trainingTypeId, exception.getMessage());
        verify(trainingTypeRepo, times(1)).findById(trainingTypeId);
    }

    @Test
    void findAll_shouldReturnListOfTrainingTypes() {
        // Arrange
        List<TrainingType> expectedTrainingTypes = List.of(
                new TrainingType(1, "Yoga"),
                new TrainingType(2, "Pilates")
        );
        when(trainingTypeRepo.findAll()).thenReturn(expectedTrainingTypes);

        // Act
        List<TrainingType> actualTrainingTypes = trainingTypeService.findAll();

        // Assert
        assertEquals(expectedTrainingTypes, actualTrainingTypes);
        verify(trainingTypeRepo, times(1)).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoTrainingTypesExist() {
        // Arrange
        when(trainingTypeRepo.findAll()).thenReturn(List.of());

        // Act
        List<TrainingType> actualTrainingTypes = trainingTypeService.findAll();

        // Assert
        assertTrue(actualTrainingTypes.isEmpty());
        verify(trainingTypeRepo, times(1)).findAll();
    }
}
