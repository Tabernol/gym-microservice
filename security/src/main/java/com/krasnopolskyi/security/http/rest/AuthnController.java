package com.krasnopolskyi.security.http.rest;


import com.krasnopolskyi.security.dto.UserCredentials;
import com.krasnopolskyi.security.exception.AuthnException;
import com.krasnopolskyi.security.exception.EntityException;
import com.krasnopolskyi.security.service.AuthenticationService;
import com.krasnopolskyi.security.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fit-coach/auth")
@Slf4j
public class AuthnController {

    private final AuthenticationService authenticationService;

    /**
     * Provide one end-point for authentication users
     * @return JWT token for further authentication
     * @throws EntityException throws if username does not exist
     * @throws AuthnException throws if password is wrong
     */
//    @Operation(summary = "User login",
//            description = "Authenticates a user and returns a JWT token for further authorization.")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserCredentials userCredentials)
            throws AuthnException {
        log.info("Cred " + userCredentials.username());
        log.info("Cred " + userCredentials.password());
        String token = authenticationService.logIn(userCredentials);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);  // Set the token in Authorization header
        return ResponseEntity.ok().headers(headers).body(token);
    }

//    @Operation(summary = "User logout")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request)
            throws AuthnException {
        authenticationService.logout(request.getHeader("Authorization"));
        return ResponseEntity.ok().body("Logged out successfully.");
    }
}
