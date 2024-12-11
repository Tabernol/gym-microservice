package com.krasnopolskyi.fitcoach.utils.mapper;

import com.krasnopolskyi.fitcoach.dto.response.UserProfileDto;
import com.krasnopolskyi.fitcoach.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static List<UserProfileDto> mapToDto(Collection<User> users) {
        return users.stream().map(user -> mapToDto(user)).collect(Collectors.toList());
    }


    public static UserProfileDto mapToDto(User user) {
        return new UserProfileDto(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName());
    }
}
