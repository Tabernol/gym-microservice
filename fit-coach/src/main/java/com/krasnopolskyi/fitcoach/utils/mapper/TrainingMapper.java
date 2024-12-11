package com.krasnopolskyi.fitcoach.utils.mapper;

import com.krasnopolskyi.fitcoach.dto.response.TrainingResponseDto;
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
}
