package com.krasnopolskyi.security.utils.mapper;

import com.krasnopolskyi.security.dto.TrainerDto;
import com.krasnopolskyi.security.dto.TrainerFullDto;
import com.krasnopolskyi.security.entity.User;

public class TrainerMapper {

    public static TrainerFullDto map(TrainerDto trainer, User user) {
        return new TrainerFullDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getIsActive(),
                trainer.getSpecialization());
    }
}
