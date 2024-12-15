package com.krasnopolskyi.report.service;

import com.krasnopolskyi.report.dto.TrainingSessionDto;
import com.krasnopolskyi.report.entity.TrainingSession;
import com.krasnopolskyi.report.repository.TrainingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainingSessionService {
    private final TrainingSessionRepository trainingSessionRepository;

    public TrainingSession saveTrainingSession(TrainingSessionDto trainingSessionDto) {
        TrainingSession trainingSession = new TrainingSession();
        trainingSession.setUsername(trainingSessionDto.username());
        trainingSession.setFirstName(trainingSessionDto.firstName());
        trainingSession.setLastName(trainingSessionDto.lastName());
        trainingSession.setActive(trainingSessionDto.isActive());
        trainingSession.setDate(trainingSessionDto.date());
        trainingSession.setDuration(trainingSessionDto.duration());
        trainingSession.setOperation(trainingSessionDto.operation());
        return trainingSessionRepository.save(trainingSession);
    }
}
