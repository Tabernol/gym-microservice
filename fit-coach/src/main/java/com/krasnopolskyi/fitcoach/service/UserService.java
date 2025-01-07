package com.krasnopolskyi.fitcoach.service;

import com.krasnopolskyi.fitcoach.entity.User;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.GymException;
import com.krasnopolskyi.fitcoach.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    private final JmsTemplate jmsTemplate;

    public User updateLocalUser(User user) throws EntityException {
        User existingUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new EntityException("Could not found user with username: " + user.getUsername()));
        existingUser.setIsActive(user.getIsActive());
        return userRepository.save(existingUser);
    }

    public void updateRemoteUser(User user) throws GymException {
            jmsTemplate.convertAndSend("user.queue", user, message -> {
                message.setStringProperty("_typeId_", "user");
                return message;
            });
    }


}
