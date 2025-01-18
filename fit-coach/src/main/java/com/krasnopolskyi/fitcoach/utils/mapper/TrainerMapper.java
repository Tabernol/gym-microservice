package com.krasnopolskyi.fitcoach.utils.mapper;


import com.krasnopolskyi.fitcoach.dto.request.trainer.TrainerDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainerProfileDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainerProfileShortDto;
import com.krasnopolskyi.fitcoach.dto.response.UserProfileDto;
import com.krasnopolskyi.fitcoach.entity.Trainee;
import com.krasnopolskyi.fitcoach.entity.Trainer;
import com.krasnopolskyi.fitcoach.entity.TrainingType;
import com.krasnopolskyi.fitcoach.entity.User;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class TrainerMapper {

    public static Trainer mapToEntity(TrainerDto dto, TrainingType specialization){
        Trainer trainer = new Trainer();

        User user = new User(
                dto.getUserId(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getUsername(),
                dto.getIsActive());


        trainer.setUser(user);
        trainer.setSpecialization(specialization);
        return trainer;
    }

    public static TrainerProfileDto mapToDto(Trainer trainer) {
        List<UserProfileDto> traineeProfiles = trainer.getTrainees()
                .stream()
                .map(Trainee::getUser)
                .map(UserMapper::mapToDto)
                .collect(Collectors.toList());

        return new TrainerProfileDto(
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getUser().getUsername(),
                trainer.getSpecialization().getType(),
                trainer.getUser().isActive(),
                traineeProfiles);
    }

    public static TrainerProfileShortDto mapToShortDto(Trainer trainer) {
        return new TrainerProfileShortDto(
                UserMapper.mapToDto(trainer.getUser()),
                trainer.getSpecialization().getType());
    }
}
