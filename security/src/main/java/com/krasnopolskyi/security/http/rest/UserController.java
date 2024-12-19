package com.krasnopolskyi.security.http.rest;

import com.krasnopolskyi.security.dto.ChangePasswordDto;
import com.krasnopolskyi.security.dto.ToggleStatusDto;
import com.krasnopolskyi.security.exception.AuthnException;
import com.krasnopolskyi.security.exception.EntityException;
import com.krasnopolskyi.security.exception.ValidateException;
import com.krasnopolskyi.security.service.UserService;
import com.krasnopolskyi.security.utils.validation.Create;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fit-coach/auth")
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Provides functionality for changing trainee status
     *
     * @param username  of target trainee
     * @param statusDto dto with username and status
     * @return message of result this action
     * @throws EntityException   if username does not exist
     * @throws ValidateException if username in pathVariable and in body are different
     */
//    @Operation(summary = "Toggle trainee status",
//            description = "Changes the status (active/inactive) of the trainee.")
//    @PreAuthorize("hasAuthority('TRAINEE')")
    @PatchMapping("/{username}/toggle-status")
    public ResponseEntity<String> toggleStatus(
            @PathVariable("username") String username,
            @Validated(Create.class) @RequestBody ToggleStatusDto statusDto) throws EntityException, ValidateException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.changeActivityStatus(username, statusDto));
    }

    /**
     * Provide possibility to change password
     * @param changePasswordDto Dto contains username, old password and new password
     * @return message 'Password has changed' otherwise throws exception
     * @throws AuthnException  throws if username does not exist
     * @throws EntityException throws if password is wrong
     */
//    @Operation(summary = "Change user password",
//            description = "Allows users to change their password by providing the current password and the new password.")
    @PutMapping("/pass/change")
    public ResponseEntity<String> changePassword(
            @Validated(Create.class) @RequestBody ChangePasswordDto changePasswordDto)
            throws AuthnException, EntityException {
        userService.changePassword(changePasswordDto);
        return ResponseEntity.status(HttpStatus.OK).body("Password has changed");
    }
}
