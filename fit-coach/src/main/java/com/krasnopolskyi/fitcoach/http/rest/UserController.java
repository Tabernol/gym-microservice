package com.krasnopolskyi.fitcoach.http.rest;

import com.krasnopolskyi.fitcoach.entity.User;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fit-coach/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    // in general this method using for update isActive status
    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) throws EntityException {
        return ResponseEntity.ok(userService.updateLocalUser(user));
    }
}
