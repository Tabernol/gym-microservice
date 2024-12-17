package com.krasnopolskyi.security.utils.mapper;

import com.krasnopolskyi.security.dto.TrainerDto;
import com.krasnopolskyi.security.dto.TrainerFullDto;
import com.krasnopolskyi.security.entity.User;

public class TrainerMapper {

    public static TrainerFullDto map(TrainerDto trainer, User user) {
        return TrainerFullDto.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .isActive(user.getIsActive())
                .specialization(trainer.getSpecialization())
                .build();
    }
}
