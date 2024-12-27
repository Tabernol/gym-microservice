package com.krasnopolskyi.service;

import com.krasnopolskyi.IntegrationTestBase;
import com.krasnopolskyi.fitcoach.repository.UserRepository;
import com.krasnopolskyi.fitcoach.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceImplIT extends IntegrationTestBase {

    @Autowired
    private UserService userServiceImpl;

    @Autowired
    private UserRepository userRepository;

//    @Test
//    void create_ShouldReturnNewUser() {
//        UserDto userDto = new UserDto("John", "Doe", "pass");
//
//        User createdUser = userServiceImpl.create(userDto);
//
//        assertNotNull(createdUser);
//        assertEquals("John", createdUser.getFirstName());
//        assertEquals("Doe", createdUser.getLastName());
//        assertTrue(createdUser.getIsActive());
//        assertNotNull(createdUser.getUsername()); // Unique username should be generated
//        assertNotNull(createdUser.getPassword()); // Password should be generated
//        assertNotNull(createdUser.getPassword());
//    }

//    @Test
//    void changeActivityStatus_ShouldUpdateIsActiveStatus() throws EntityException {
//        ToggleStatusDto toggleStatusDto = new ToggleStatusDto("john.doe", false);
//
//        User updatedUser = userServiceImpl.changeActivityStatus(toggleStatusDto);
//
//        assertNotNull(updatedUser);
//        assertFalse(updatedUser.getIsActive()); // User's active status should be updated
//    }

//    @Test
//    void generateUsername_ShouldReturnUniqueUsername_WhenBaseUsernameAlreadyExists() {
//        // Given: a user with the base username "john.doe" exists
//        User existingUser = new User();
//        existingUser.setUsername("john.doe");
//        existingUser.setFirstName("John");
//        existingUser.setLastName("Doe");
//        existingUser.setPassword("password");
//        existingUser.setIsActive(true);
//
//        // When: a new user with the same first and last name is created
//        String newUsername = userServiceImpl.create(new UserDto("John", "Doe", "pass")).getUsername();
//
//        // Then: the new username should be "john.doe1"
//        assertEquals("john.doe1", newUsername);
//    }
}
