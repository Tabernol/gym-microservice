package com.krasnopolskyi.fitcoach.service;

import com.krasnopolskyi.fitcoach.entity.User;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User updateLocalUser(User user) throws EntityException {
        User existingUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new EntityException("Could not found user with username: " + user.getUsername()));
        existingUser.setIsActive(user.getIsActive());
        return userRepository.save(existingUser);
    }



}
