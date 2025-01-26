package com.krasnopolskyi.security.utils.mapper;

import com.krasnopolskyi.security.dto.TrainerDto;
import com.krasnopolskyi.security.dto.TrainerFullDto;
import com.krasnopolskyi.security.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TrainerMapper {

    public static TrainerFullDto map(TrainerDto trainer, User user) {
        return new TrainerFullDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.isActive(),
                trainer.getSpecialization());
    }
}
