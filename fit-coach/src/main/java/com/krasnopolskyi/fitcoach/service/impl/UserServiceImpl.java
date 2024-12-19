package com.krasnopolskyi.fitcoach.service.impl;

import com.krasnopolskyi.fitcoach.dto.request.user.ChangePasswordDto;
import com.krasnopolskyi.fitcoach.dto.request.user.ToggleStatusDto;
import com.krasnopolskyi.fitcoach.dto.response.UserDto;
import com.krasnopolskyi.fitcoach.entity.User;
import com.krasnopolskyi.fitcoach.exception.AuthnException;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.repository.UserRepository;
import com.krasnopolskyi.fitcoach.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private User findByUsername(String username) throws EntityException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityException("Could not found user: " + username));
    }


//    @Transactional
//    @Override
//    public User changePassword(ChangePasswordDto changePasswordDto) throws EntityException, AuthnException {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if(!authentication.getName().equals(changePasswordDto.username())){
//            AuthnException exception = new AuthnException("You do not have the necessary permissions to access this resource.");
//            exception.setCode(403);
//            throw exception;
//        }
//        User user = findByUsername(changePasswordDto.username());
//
//        if(!passwordEncoder.matches(changePasswordDto.oldPassword(), user.getPassword())){
//            AuthnException authnException = new AuthnException("Bad Credentials");
//            authnException.setCode(HttpStatus.UNAUTHORIZED.value());
//            throw authnException;
//        }
//        validatePassword(changePasswordDto.newPassword());
//        user.setPassword(passwordEncoder.encode(changePasswordDto.newPassword()));
//        return userRepository.save(user);
//    }

//    @Override
//    public User changeActivityStatus(ToggleStatusDto statusDto) throws EntityException {
//        User user = findByUsername(statusDto.username());
//        user.setIsActive(statusDto.isActive()); //status changes here
//        return userRepository.save(user);
//    }


//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return userRepository.findByUsername(username)
//                .map(user -> {
//                    return new org.springframework.security.core.userdetails.User(
//                            user.getUsername(),
//                            user.getPassword(),
//                            user.getRoles());  // Pass GrantedAuthorities to UserDetails
//                })
//                .orElseThrow(() -> new UsernameNotFoundException("Failed to retrieve user: " + username));
//    }
}
