package com.krasnopolskyi.security.utils.mapper;

import com.krasnopolskyi.security.dto.TraineeDto;
import com.krasnopolskyi.security.dto.TraineeFullDto;
import com.krasnopolskyi.security.entity.User;

public class TraineeMapper {

    public static TraineeFullDto map(TraineeDto trainee, User user) {
        return new TraineeFullDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getIsActive(),
                trainee.getDateOfBirth(),
                trainee.getAddress()
        );
    }
}
