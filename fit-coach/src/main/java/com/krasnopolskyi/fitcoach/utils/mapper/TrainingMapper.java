package com.krasnopolskyi.fitcoach.utils.mapper;

import com.krasnopolskyi.fitcoach.dto.request.training.TrainingSessionDto;
import com.krasnopolskyi.fitcoach.dto.request.training.TrainingSessionOperation;
import com.krasnopolskyi.fitcoach.dto.response.TrainingResponseDto;
import com.krasnopolskyi.fitcoach.entity.Trainer;
import com.krasnopolskyi.fitcoach.entity.Training;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TrainingMapper {

    public TrainingResponseDto mapToDto(Training training) {
        String trainerFullName = training.getTrainer().getUser().getFirstName() + " "
                + training.getTrainer().getUser().getLastName();
        String traineeFullName = training.getTrainee().getUser().getFirstName() + " "
                + training.getTrainee().getUser().getLastName();

        return new TrainingResponseDto(
                training.getId(),
                training.getTrainingName(),
                training.getTrainingType().getType(),
                trainerFullName,
                traineeFullName,
                training.getDate(),
                training.getDuration());
    }

    public TrainingSessionDto mapToDto(Training training, Trainer trainer, TrainingSessionOperation operation) {
        return new TrainingSessionDto(
                training.getId(),
                trainer.getUser().getUsername(),
                training.getDate(),
                training.getDuration(),
                operation);

    }
}
