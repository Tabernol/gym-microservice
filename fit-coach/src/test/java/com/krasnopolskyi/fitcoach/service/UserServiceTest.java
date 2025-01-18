package com.krasnopolskyi.fitcoach.service;

import com.krasnopolskyi.fitcoach.entity.User;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.GymException;
import com.krasnopolskyi.fitcoach.http.client.SecurityModuleClient;
import com.krasnopolskyi.fitcoach.repository.UserRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityModuleClient securityModuleClient;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("testUser");
        user.setIsActive(true);
    }

    @Test
    void testUpdateLocalUser_Success() throws EntityException {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.updateLocalUser(user);

        assertEquals(user.getUsername(), updatedUser.getUsername());
        assertEquals(user.getIsActive(), updatedUser.getIsActive());
    }

    @Test
    void testUpdateLocalUser_UserNotFound() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        EntityException thrown = assertThrows(EntityException.class, () -> {
            userService.updateLocalUser(user);
        });

        assertEquals("Could not found user with username: " + user.getUsername(), thrown.getMessage());
    }

//    @Test
//    void testUpdateRemoteUser_Success() throws GymException {
//        // Arrange
//        when(securityModuleClient.updateUserData(user)).thenReturn(ResponseEntity.ok(user));
//
//        // Act
//        userService.updateRemoteUser(user);
//
//        // Assert
//        assertNotNull(updatedUser);
//        assertEquals(user.getUsername(), updatedUser.getUsername());
//        verify(securityModuleClient, times(1)).updateUserData(user);
//    }

    @Test
    void testUpdateRemoteUser_FailureFeignException() throws GymException {
        // Arrange
        FeignException feignException = mock(FeignException.class);
        when(securityModuleClient.updateUserData(user)).thenThrow(feignException);

        // Act & Assert
        GymException thrown = assertThrows(GymException.class, () -> {
            userService.updateRemoteUser(user);
        });

        assertEquals("Internal error occurred while communicating with another microservice", thrown.getMessage());
        verify(securityModuleClient, times(1)).updateUserData(user);
    }
}
