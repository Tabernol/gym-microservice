package com.krasnopolskyi.service;

import com.krasnopolskyi.fitcoach.entity.TrainingType;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.IntegrationTestBase;
import com.krasnopolskyi.fitcoach.repository.TrainingTypeRepository;
import com.krasnopolskyi.fitcoach.service.TrainingTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TrainingTypeServiceIT extends IntegrationTestBase {

    @Autowired
    private TrainingTypeService trainingTypeService;

    @Autowired
    private TrainingTypeRepository trainingTypeRepo;

    @Test
    void findById_ShouldReturnTrainingType_WhenExists() throws EntityException {
        TrainingType foundType = trainingTypeService.findById(1);

        assertNotNull(foundType);
        assertEquals(1, foundType.getId());
        assertEquals("Bodybuilding", foundType.getType());
    }

    @Test
    void findById_ShouldThrowEntityException_WhenNotExists() {
        // Non-existent ID
        Integer nonExistentId = 999;

        EntityException thrown = assertThrows(
                EntityException.class,
                () -> trainingTypeService.findById(nonExistentId)
        );

        assertEquals("Could not find training type with id " + nonExistentId, thrown.getMessage());
    }

    @Test
    void findAll_ShouldReturnAllTrainingTypes() {
        List<TrainingType> allTrainingTypes = trainingTypeService.findAll();

        assertNotNull(allTrainingTypes);
        assertTrue(allTrainingTypes.size() == 5);
    }
}
