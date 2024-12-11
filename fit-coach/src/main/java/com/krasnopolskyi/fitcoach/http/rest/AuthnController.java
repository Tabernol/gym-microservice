package com.krasnopolskyi.fitcoach.http.rest;

import com.krasnopolskyi.fitcoach.dto.request.ChangePasswordDto;
import com.krasnopolskyi.fitcoach.dto.request.UserCredentials;
import com.krasnopolskyi.fitcoach.exception.AuthnException;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.service.AuthenticationService;
import com.krasnopolskyi.fitcoach.service.UserService;
import com.krasnopolskyi.fitcoach.validation.Create;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fit-coach/authn")
public class AuthnController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    /**
     * Provide one end-point for authentication users
     * @return JWT token for further authentication
     * @throws EntityException throws if username does not exist
     * @throws AuthnException throws if password is wrong
     */
    @Operation(summary = "User login",
            description = "Authenticates a user and returns a JWT token for further authorization.")
    @PostMapping("/login")
    public ResponseEntity<String> login( @RequestBody UserCredentials userCredentials)
            throws EntityException, AuthnException {
        String token = authenticationService.logIn(userCredentials);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);  // Set the token in Authorization header
        return ResponseEntity.ok().headers(headers).body(token);
    }

    @Operation(summary = "User logout")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request)
            throws EntityException, AuthnException {
        authenticationService.logout(request.getHeader("Authorization"));
        return ResponseEntity.ok().body("Logged out successfully.");
    }


    /**
     * Provide possibility to change password
     * @param changePasswordDto Dto contains username, old password and new password
     * @return message 'Password has changed' otherwise throws exception
     * @throws AuthnException  throws if username does not exist
     * @throws EntityException throws if password is wrong
     */
    @Operation(summary = "Change user password",
            description = "Allows users to change their password by providing the current password and the new password.")
    @PutMapping("/pass/change")
    public ResponseEntity<String> changePassword(
            @Validated(Create.class) @RequestBody ChangePasswordDto changePasswordDto)
            throws AuthnException, EntityException {
        userService.changePassword(changePasswordDto);
        return ResponseEntity.status(HttpStatus.OK).body("Password has changed");
    }
}
