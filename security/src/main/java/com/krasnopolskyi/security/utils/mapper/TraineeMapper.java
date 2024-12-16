package com.krasnopolskyi.security.utils.mapper;

import com.krasnopolskyi.security.dto.TraineeDto;
import com.krasnopolskyi.security.dto.TraineeFullDto;
import com.krasnopolskyi.security.entity.User;

public class TraineeMapper {

    public static TraineeFullDto map(TraineeDto trainee, User user) {
        return TraineeFullDto.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .address(trainee.getAddress())
                .dateOfBirth(trainee.getDateOfBirth())
                .isActive(user.getIsActive())
                .build();
    }
}
