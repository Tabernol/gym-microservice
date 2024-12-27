package com.krasnopolskyi.security.utils.mapper;

import com.krasnopolskyi.security.dto.UserDto;
import com.krasnopolskyi.security.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public static UserDto mapToDto(User user){
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getIsActive()
        );
    }
}
