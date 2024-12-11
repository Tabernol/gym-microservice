package com.krasnopolskyi.fitcoach.utils.mapper;

import com.krasnopolskyi.fitcoach.dto.request.TraineeDto;
import com.krasnopolskyi.fitcoach.dto.response.TraineeProfileDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainerProfileDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainerProfileShortDto;
import com.krasnopolskyi.fitcoach.dto.response.UserProfileDto;
import com.krasnopolskyi.fitcoach.entity.Trainee;
import com.krasnopolskyi.fitcoach.entity.Trainer;
import com.krasnopolskyi.fitcoach.entity.User;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class TraineeMapper {

    public static TraineeProfileDto mapToDto(Trainee trainee) {
        List<TrainerProfileShortDto> trainerProfiles = new ArrayList<>();
        for (Trainer trainer : trainee.getTrainers()) {
            UserProfileDto userProfileDto = UserMapper.mapToDto(trainer.getUser());
            trainerProfiles.add(new TrainerProfileShortDto(userProfileDto, trainer.getSpecialization().getType()));
        }
        TraineeProfileDto traineeProfileDto = TraineeProfileDto.builder()
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .username(trainee.getUser().getUsername())
                .address(trainee.getAddress())
                .dateOfBirth(trainee.getDateOfBirth())
                .isActive(trainee.getUser().getIsActive())
                .trainers(trainerProfiles)
                .build();

        return traineeProfileDto;
    }

    public static Trainee mapToEntity(TraineeDto traineeDto, User user){
        Trainee trainee = new Trainee();
        trainee.setAddress(traineeDto.getAddress());
        trainee.setDateOfBirth(traineeDto.getDateOfBirth());
        trainee.setUser(user);
        return trainee;
    }
}
