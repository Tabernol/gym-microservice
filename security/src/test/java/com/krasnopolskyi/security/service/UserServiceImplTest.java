package com.krasnopolskyi.security.service;

import com.krasnopolskyi.security.dto.*;
import com.krasnopolskyi.security.entity.Role;
import com.krasnopolskyi.security.entity.User;
import com.krasnopolskyi.security.exception.AuthnException;
import com.krasnopolskyi.security.exception.EntityException;
import com.krasnopolskyi.security.exception.GymException;
import com.krasnopolskyi.security.exception.ValidateException;
import com.krasnopolskyi.security.http.client.FitCoachClient;
import com.krasnopolskyi.security.repo.UserRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private FitCoachClient fitCoachClient;

    private User mockUser;
    //    private UserDto mockUserDto;
    private UserCredentials mockCredentials;
    private ChangePasswordDto mockChangePasswordDto;
    private ToggleStatusDto mockToggleStatusDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userServiceImpl = new UserServiceImpl(userRepository, passwordEncoder, fitCoachClient); // inject mock repository
        // Setup mock User
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setUsername("john.doe");
        mockUser.setPassword("password123");
        mockUser.setRoles(Set.of(Role.TRAINEE));
        mockUser.setIsActive(true);

//        // Setup mock UserDto
//        mockUserDto = new UserDto("John", "Doe", "root");

        // Setup mock credentials
        mockCredentials = new UserCredentials("john.doe", "password123");

        // Setup mock ChangePasswordDto
        mockChangePasswordDto = new ChangePasswordDto("john.doe", "password123", "newPassword123");

        // Setup mock ToggleStatusDto
        mockToggleStatusDto = new ToggleStatusDto("john.doe", false);
    }

    @Test
    void changePassword_ShouldChangePasswordSuccessfully() throws EntityException, AuthnException {
        // Arrange
        ChangePasswordDto dto = new ChangePasswordDto("user1", "oldPassword", "newPassword");
        User mockUser = new User();
        mockUser.setUsername("user1");
        mockUser.setPassword("encodedOldPassword");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user1");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUsername(dto.username())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(dto.oldPassword(), mockUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(dto.newPassword())).thenReturn("encodedNewPassword");
        when(userRepository.save(mockUser)).thenReturn(mockUser);

        // Act
        User updatedUser = userServiceImpl.changePassword(dto);

        // Assert
        assertNotNull(updatedUser);
        verify(userRepository, times(1)).save(mockUser);
        assertEquals("encodedNewPassword", updatedUser.getPassword());
    }

    @Test
    void changePassword_ShouldThrowAuthnException_WhenPasswordsDoNotMatch() {
        // Arrange
        ChangePasswordDto dto = new ChangePasswordDto("user1", "wrongOldPassword", "newPassword");
        User mockUser = new User();
        mockUser.setUsername("user1");
        mockUser.setPassword("encodedOldPassword");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user1");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUsername(dto.username())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(dto.oldPassword(), mockUser.getPassword())).thenReturn(false);

        // Act & Assert
        AuthnException exception = assertThrows(AuthnException.class, () -> userServiceImpl.changePassword(dto));
        assertEquals("Bad Credentials", exception.getMessage());
    }

    @Test
    void changePassword_ShouldThrowAuthnException_WhenUserMismatch() {
        // Arrange
        ChangePasswordDto dto = new ChangePasswordDto("user2", "oldPassword", "newPassword");
        User mockUser = new User();
        mockUser.setUsername("user1");
        mockUser.setPassword("encodedOldPassword");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user1");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act & Assert
        AuthnException exception = assertThrows(AuthnException.class, () -> userServiceImpl.changePassword(dto));
        assertEquals("You do not have the necessary permissions to access this resource.", exception.getMessage());
    }

    @Test
    void changeActivityStatus_ShouldChangeStatusSuccessfully() throws EntityException, ValidateException {
        // Arrange
        String username = "user1";
        ToggleStatusDto statusDto = new ToggleStatusDto(username, true);
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setIsActive(false);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(mockUser)).thenReturn(mockUser);

        // Act
        String result = userServiceImpl.changeActivityStatus(username, statusDto);

        // Assert
        assertEquals("user1 is active", result);
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void saveTrainee_ShouldCallFitCoachServiceAndSaveUser() throws GymException {
        // Arrange
        TraineeDto traineeDto = new TraineeDto("John", "Doe", LocalDate.of(2000, 10, 10), "address");
        User mockUser = new User();
        mockUser.setId(12L);
        mockUser.setUsername("john.doe");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setIsActive(true);


        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(fitCoachClient.saveTrainee(any(TraineeFullDto.class)))
                .thenReturn(ResponseEntity.ofNullable(null));

        // Act
        UserCredentials result = userServiceImpl.saveTrainee(traineeDto);

        // Assert
        assertNotNull(result);
        assertEquals("john.doe", result.username());
        verify(fitCoachClient, times(1)).saveTrainee(any(TraineeFullDto.class));
    }

    @Test
    void saveTrainee_ShouldThrowGymException_WhenFeignClientFails() throws GymException {
        // Arrange
        TraineeDto traineeDto = new TraineeDto("John", "Doe", LocalDate.of(2000, 10, 10), "address");
        User mockUser = new User();
        mockUser.setId(12L);
        mockUser.setUsername("john.doe");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setIsActive(true);

        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(fitCoachClient.saveTrainee(any(TraineeFullDto.class))).thenThrow(FeignException.class);

        // Act & Assert
        GymException exception = assertThrows(GymException.class, () -> userServiceImpl.saveTrainee(traineeDto));
        assertEquals("Internal error occurred while communicating with another microservice", exception.getMessage());
    }

    @Test
    void saveTrainer_ShouldCallFitCoachServiceAndSaveUser() throws GymException {
        // Arrange
        TrainerDto trainerDto = new TrainerDto("John", "Doe", 1);
        User mockUser = new User();
        mockUser.setId(12L);
        mockUser.setUsername("john.doe");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setIsActive(true);

        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(fitCoachClient.saveTrainer(any(TrainerFullDto.class)))
                .thenReturn(ResponseEntity.ofNullable(null));

        // Act
        UserCredentials result = userServiceImpl.saveTrainer(trainerDto);

        // Assert
        assertNotNull(result);
        assertEquals("john.doe", result.username());
        verify(fitCoachClient, times(1)).saveTrainer(any(TrainerFullDto.class));
    }

    @Test
    void saveTrainer_UsernameAlreadyExist_ShouldCallFitCoachServiceAndSaveUser() throws GymException {
        // Arrange
        TrainerDto trainerDto = new TrainerDto("John", "Doe", 1);
        User user1 = new User();
        user1.setId(11L);
        user1.setUsername("john.doe");
        user1.setFirstName("John");
        user1.setLastName("Doe");

        User mockUser = new User();
        mockUser.setId(12L);
        mockUser.setUsername("john.doe2");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setIsActive(true);

        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.ofNullable(user1));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(fitCoachClient.saveTrainer(any(TrainerFullDto.class)))
                .thenReturn(ResponseEntity.ofNullable(null));

        // Act
        UserCredentials result = userServiceImpl.saveTrainer(trainerDto);

        // Assert
        assertNotNull(result);
        assertEquals("john.doe2", result.username());
        verify(fitCoachClient, times(1)).saveTrainer(any(TrainerFullDto.class));
    }

    @Test
    void saveTrainer_ShouldThrowGymException_WhenFeignClientFails() throws GymException {
        // Arrange
        TrainerDto trainerDto = new TrainerDto("John", "Doe", 1);
        User mockUser = new User();
        mockUser.setId(12L);
        mockUser.setUsername("john.doe");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setIsActive(true);

        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(fitCoachClient.saveTrainer(any(TrainerFullDto.class))).thenThrow(FeignException.class);

        // Act & Assert
        GymException exception = assertThrows(GymException.class, () -> userServiceImpl.saveTrainer(trainerDto));
        assertEquals("Internal error occurred while communicating with another microservice", exception.getMessage());
    }


//    @Test
//    void testCreateUser() {
//        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.empty());
//
//        User result = userServiceImpl.create(mockUserDto);
//
//        assertNotNull(result);
//        assertEquals(mockUserDto.firstName(), result.getFirstName());
//        assertEquals(mockUserDto.lastName(), result.getLastName());
//        assertTrue(result.getIsActive());
//        assertNotNull(result.getUsername());
//    }
//
//    @Test
//    void testCreateUserIfUsernameExist() {
//        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.ofNullable(mockUser));
//        when(userRepository.findByUsername("john.doe1")).thenReturn(Optional.empty());
//
//        User result = userServiceImpl.create(mockUserDto);
//
//        assertNotNull(result);
//        assertEquals(mockUserDto.firstName(), result.getFirstName());
//        assertEquals(mockUserDto.lastName(), result.getLastName());
//        assertTrue(result.getIsActive());
//        assertNotNull(result.getUsername());
//    }
//
//    @Test
//    void testChangePasswordSuccess() throws GymException {
//        // Mock the authentication
//        Authentication authentication = mock(Authentication.class);
//        SecurityContext securityContext = mock(SecurityContext.class);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getName()).thenReturn(mockChangePasswordDto.username());  // Simulate the authenticated user
//        SecurityContextHolder.setContext(securityContext);
//
//        // Mock password matching and user repository behavior
//        when(passwordEncoder.matches(any(), anyString())).thenReturn(true);
//        when(userRepository.findByUsername(mockChangePasswordDto.username()))
//                .thenReturn(Optional.of(mockUser));
//        when(userRepository.save(any(User.class))).thenReturn(mockUser);
//
//        // Call the method under test
//        User updatedUser = userServiceImpl.changePassword(mockChangePasswordDto);
//
//        // Assertions
//        assertNotNull(updatedUser);
//        verify(userRepository).save(mockUser);  // Ensure the user is saved
//    }
//
//    @Test
//    void testChangePasswordFailWrongOldPassword() {
//        // Mock the authentication
//        Authentication authentication = mock(Authentication.class);
//        SecurityContext securityContext = mock(SecurityContext.class);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getName()).thenReturn(mockChangePasswordDto.username());  // Simulate the authenticated user
//        SecurityContextHolder.setContext(securityContext);
//
//        // Mock user repository to return a valid user
//        when(userRepository.findByUsername(mockChangePasswordDto.username()))
//                .thenReturn(Optional.of(mockUser));
//
//        // Mock password mismatch
//        when(passwordEncoder.matches(any(), anyString())).thenReturn(false);
//
//        // Prepare test DTO with wrong old password
//        ChangePasswordDto wrongOldPasswordDto = new ChangePasswordDto(
//                "john.doe", "wrongOldPassword", "newPassword123");
//
//        // Assert that an AuthnException is thrown due to wrong old password
//        assertThrows(AuthnException.class, () -> userServiceImpl.changePassword(wrongOldPasswordDto));
//    }
//
//    @Test
//    void testChangePasswordFailMissMatchUsername() {
//
//        ChangePasswordDto changePasswordDto = new ChangePasswordDto("another.doe", "password123", "newPassword123");
//        // Mock the authentication
//        Authentication authentication = mock(Authentication.class);
//        SecurityContext securityContext = mock(SecurityContext.class);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getName()).thenReturn(mockUser.getUsername());  // Simulate the authenticated user
//        SecurityContextHolder.setContext(securityContext);
//
//        assertThrows(AuthnException.class, () -> userServiceImpl.changePassword(changePasswordDto));
//    }

    @Test
    void testChangeActivityStatus() throws EntityException, ValidateException {
        when(userRepository.findByUsername(mockToggleStatusDto.username()))
                .thenReturn(Optional.of(mockUser));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(mockUser);

        String result = userServiceImpl.changeActivityStatus(mockUser.getUsername(), mockToggleStatusDto);

        String status = mockUser.getIsActive() ? " is active" : " is inactive";
        assertNotNull(result);
    }

    @Test
    void testChangeActivityStatus_ValidateException() throws EntityException, ValidateException {
        assertThrows(ValidateException.class,
                () -> userServiceImpl.changeActivityStatus("another.name", mockToggleStatusDto));
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
