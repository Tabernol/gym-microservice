package com.krasnopolskyi.fitcoach.http.rest;

import com.krasnopolskyi.fitcoach.entity.User;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("testUser");
        user.setIsActive(true);
    }

    @Test
    void testUpdateUser_Success() throws EntityException {
        when(userService.updateLocalUser(user)).thenReturn(user);

        ResponseEntity<User> response = userController.updateUser(user);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
    }

    @Test
    void testUpdateUser_ThrowsEntityException() throws EntityException {
        when(userService.updateLocalUser(user)).thenThrow(new EntityException("Could not found user with username: " + user.getUsername()));

        EntityException thrown = org.junit.jupiter.api.Assertions.assertThrows(EntityException.class, () -> {
            userController.updateUser(user);
        });

        assertEquals("Could not found user with username: " + user.getUsername(), thrown.getMessage());
    }
}
