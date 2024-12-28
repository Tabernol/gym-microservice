package com.krasnopolskyi.fitcoach.http.client;

import com.krasnopolskyi.fitcoach.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "security", url = "http://localhost:8765/api/v1/fit-coach/auth")
public interface SecurityModuleClient {

    // update user data: firstname, lastname, active
    @PostMapping("/users")
    ResponseEntity<User> updateUserData(@RequestBody User user);
}
