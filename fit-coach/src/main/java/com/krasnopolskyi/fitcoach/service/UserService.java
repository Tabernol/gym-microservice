package com.krasnopolskyi.fitcoach.service;

import com.krasnopolskyi.fitcoach.entity.User;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.GymException;
import com.krasnopolskyi.fitcoach.http.client.SecurityModuleClient;
import com.krasnopolskyi.fitcoach.repository.UserRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final SecurityModuleClient securityModuleClient;
    private final UserRepository userRepository;

    public User updateLocalUser(User user) throws EntityException {
        User existingUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new EntityException("Could not found user with username: " + user.getUsername()));
        existingUser.setIsActive(user.getIsActive());
        return userRepository.save(existingUser);
    }

    public User updateRemoteUser(User user) throws GymException {
        try {
            // call to security MS using feign client throws exception if failed
            return securityModuleClient.updateUserData(user).getBody();
        } catch (FeignException e) {
            log.error("Failed to update user details in security microservice with status: ", e);
            throw new GymException("Internal error occurred while communicating with another microservice");
        }
    }


}
