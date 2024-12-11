package com.krasnopolskyi.fitcoach.service;

import com.krasnopolskyi.fitcoach.dto.request.ChangePasswordDto;
import com.krasnopolskyi.fitcoach.dto.request.ToggleStatusDto;
import com.krasnopolskyi.fitcoach.dto.request.UserCredentials;
import com.krasnopolskyi.fitcoach.dto.response.UserDto;
import com.krasnopolskyi.fitcoach.entity.Role;
import com.krasnopolskyi.fitcoach.entity.User;
import com.krasnopolskyi.fitcoach.exception.AuthnException;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.GymException;
import com.krasnopolskyi.fitcoach.repository.UserRepository;
import com.krasnopolskyi.fitcoach.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User mockUser;
    private UserDto mockUserDto;
    private UserCredentials mockCredentials;
    private ChangePasswordDto mockChangePasswordDto;
    private ToggleStatusDto mockToggleStatusDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userServiceImpl = new UserServiceImpl(userRepository, passwordEncoder); // inject mock repository
        // Setup mock User
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setUsername("john.doe");
        mockUser.setPassword("password123");
        mockUser.setRoles(Set.of(Role.TRAINEE));
        mockUser.setIsActive(true);

        // Setup mock UserDto
        mockUserDto = new UserDto("John", "Doe", "root");

        // Setup mock credentials
        mockCredentials = new UserCredentials("john.doe", "password123");

        // Setup mock ChangePasswordDto
        mockChangePasswordDto = new ChangePasswordDto("john.doe", "password123", "newPassword123");

        // Setup mock ToggleStatusDto
        mockToggleStatusDto = new ToggleStatusDto("john.doe", false);
    }

    @Test
    void testCreateUser() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.empty());

        User result = userServiceImpl.create(mockUserDto);

        assertNotNull(result);
        assertEquals(mockUserDto.firstName(), result.getFirstName());
        assertEquals(mockUserDto.lastName(), result.getLastName());
        assertTrue(result.getIsActive());
        assertNotNull(result.getUsername());
    }

    @Test
    void testCreateUserIfUsernameExist() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.ofNullable(mockUser));
        when(userRepository.findByUsername("john.doe1")).thenReturn(Optional.empty());

        User result = userServiceImpl.create(mockUserDto);

        assertNotNull(result);
        assertEquals(mockUserDto.firstName(), result.getFirstName());
        assertEquals(mockUserDto.lastName(), result.getLastName());
        assertTrue(result.getIsActive());
        assertNotNull(result.getUsername());
    }

    @Test
    void testChangePasswordSuccess() throws GymException {
        // Mock the authentication
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockChangePasswordDto.username());  // Simulate the authenticated user
        SecurityContextHolder.setContext(securityContext);

        // Mock password matching and user repository behavior
        when(passwordEncoder.matches(any(), anyString())).thenReturn(true);
        when(userRepository.findByUsername(mockChangePasswordDto.username()))
                .thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Call the method under test
        User updatedUser = userServiceImpl.changePassword(mockChangePasswordDto);

        // Assertions
        assertNotNull(updatedUser);
        verify(userRepository).save(mockUser);  // Ensure the user is saved
    }

    @Test
    void testChangePasswordFailWrongOldPassword() {
        // Mock the authentication
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockChangePasswordDto.username());  // Simulate the authenticated user
        SecurityContextHolder.setContext(securityContext);

        // Mock user repository to return a valid user
        when(userRepository.findByUsername(mockChangePasswordDto.username()))
                .thenReturn(Optional.of(mockUser));

        // Mock password mismatch
        when(passwordEncoder.matches(any(), anyString())).thenReturn(false);

        // Prepare test DTO with wrong old password
        ChangePasswordDto wrongOldPasswordDto = new ChangePasswordDto(
                "john.doe", "wrongOldPassword", "newPassword123");

        // Assert that an AuthnException is thrown due to wrong old password
        assertThrows(AuthnException.class, () -> userServiceImpl.changePassword(wrongOldPasswordDto));
    }

    @Test
    void testChangePasswordFailMissMatchUsername() {

        ChangePasswordDto changePasswordDto = new ChangePasswordDto("another.doe", "password123", "newPassword123");
        // Mock the authentication
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(mockUser.getUsername());  // Simulate the authenticated user
        SecurityContextHolder.setContext(securityContext);

        assertThrows(AuthnException.class, () -> userServiceImpl.changePassword(changePasswordDto));
    }

    @Test
    void testChangeActivityStatus() throws EntityException {
        when(userRepository.findByUsername(mockToggleStatusDto.username()))
                .thenReturn(Optional.of(mockUser));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(mockUser);

        User updatedUser = userServiceImpl.changeActivityStatus(mockToggleStatusDto);

        assertNotNull(updatedUser);
        assertFalse(updatedUser.getIsActive());
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        // Mock the userRepository to return the mock user when findByUsername is called
        when(userRepository.findByUsername(mockUser.getUsername())).thenReturn(Optional.of(mockUser));

        // Call the loadUserByUsername method
        UserDetails userDetails = userServiceImpl.loadUserByUsername(mockUser.getUsername());

        // Verify the user details returned
        assertNotNull(userDetails);
        assertEquals(mockUser.getUsername(), userDetails.getUsername());

        // Verify the interaction with the repository
        verify(userRepository).findByUsername(mockUser.getUsername());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Mock the userRepository to return an empty Optional when findByUsername is called
        when(userRepository.findByUsername(mockUser.getUsername())).thenReturn(Optional.empty());

        // Call the loadUserByUsername method and assert that it throws UsernameNotFoundException
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userServiceImpl.loadUserByUsername(mockUser.getUsername()));

        // Verify the exception message
        assertEquals("Failed to retrieve user: " + mockUser.getUsername(), exception.getMessage());

        // Verify the interaction with the repository
        verify(userRepository).findByUsername(mockUser.getUsername());
    }

}
