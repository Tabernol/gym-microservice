package com.krasnopolskyi.security.utils.mapper;

import com.krasnopolskyi.security.dto.TraineeDto;
import com.krasnopolskyi.security.dto.TraineeFullDto;
import com.krasnopolskyi.security.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TraineeMapper {

    public static TraineeFullDto map(TraineeDto trainee, User user) {
        return new TraineeFullDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.isActive(),
                trainee.getDateOfBirth(),
                trainee.getAddress()
        );
    }
}
