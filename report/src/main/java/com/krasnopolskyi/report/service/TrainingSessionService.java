package com.krasnopolskyi.report.service;

import com.krasnopolskyi.report.entity.TrainingSession;
import com.krasnopolskyi.report.repository.TrainingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrainingSessionService {
    private final TrainingSessionRepository trainingSessionRepository;

    @Transactional
    public TrainingSession saveTrainingSession(TrainingSession trainingSession) {
        return trainingSessionRepository.save(trainingSession);
    }

    @Transactional
    public boolean deleteTrainingSessionById(long id){
        return trainingSessionRepository.findById(id)
                .map(entity -> {
                    trainingSessionRepository.delete(entity);
                    trainingSessionRepository.flush();
                    return true;
                })
                .orElse(false);
    }
}
