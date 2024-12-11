package com.krasnopolskyi.fitcoach.service;


import com.krasnopolskyi.fitcoach.entity.TrainingType;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.repository.TrainingTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepo;

    public TrainingType findById(Integer id) throws EntityException {
        return trainingTypeRepo.findById(id)
                .orElseThrow(() -> new EntityException("Could not find training type with id " + id));
    }

    @Transactional(readOnly = true)
    public List<TrainingType> findAll() {
        return trainingTypeRepo.findAll();
    }

}
