package com.krasnopolskyi.security.http.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.krasnopolskyi.security.dto.ChangePasswordDto;
import com.krasnopolskyi.security.dto.ToggleStatusDto;
import com.krasnopolskyi.security.dto.UserDto;
import com.krasnopolskyi.security.entity.User;
import com.krasnopolskyi.security.exception.AuthnException;
import com.krasnopolskyi.security.exception.EntityException;
import com.krasnopolskyi.security.exception.GymException;
import com.krasnopolskyi.security.exception.ValidateException;
import com.krasnopolskyi.security.service.AuthenticationService;
import com.krasnopolskyi.security.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void changePassword_ShouldReturnSuccessMessage_WhenPasswordIsChanged() throws GymException {
        // Arrange
        ChangePasswordDto changePasswordDto = new ChangePasswordDto("username", "oldPassword", "newPassword");

        // Act
        ResponseEntity<String> response = userController.changePassword(changePasswordDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password has changed", response.getBody());

        // Verify that the userService's changePassword method is called
        verify(userService, times(1)).changePassword(changePasswordDto);
    }

    @Test
    void toggleStatus_ShouldReturnSuccessMessage_WhenStatusIsToggled() throws EntityException, ValidateException, AuthnException {
        // Arrange
        String username = "john";
        ToggleStatusDto toggleStatusDto = new ToggleStatusDto(username, true);
        when(userService.changeActivityStatus(username, toggleStatusDto)).thenReturn("Status changed");

        // Act
        ResponseEntity<String> response = userController.toggleStatus(username, toggleStatusDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Status changed", response.getBody());

        // Verify that the userService's changeActivityStatus method is called
        verify(userService, times(1)).changeActivityStatus(username, toggleStatusDto);
    }


}
