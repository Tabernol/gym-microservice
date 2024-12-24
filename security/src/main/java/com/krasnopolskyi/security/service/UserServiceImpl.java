package com.krasnopolskyi.security.service;

import com.krasnopolskyi.security.dto.*;
import com.krasnopolskyi.security.entity.User;
import com.krasnopolskyi.security.exception.AuthnException;
import com.krasnopolskyi.security.exception.EntityException;
import com.krasnopolskyi.security.exception.GymException;
import com.krasnopolskyi.security.exception.ValidateException;
import com.krasnopolskyi.security.http.client.FitCoachClient;
import com.krasnopolskyi.security.password_generator.PasswordGenerator;
import com.krasnopolskyi.security.repo.UserRepository;
import com.krasnopolskyi.security.utils.mapper.TraineeMapper;
import com.krasnopolskyi.security.utils.mapper.TrainerMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FitCoachClient fitCoachClient;

    private User findByUsername(String username) throws EntityException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityException("Could not found user: " + username));
    }


    @Transactional
    @Override
    public User changePassword(ChangePasswordDto changePasswordDto) throws EntityException, AuthnException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(!authentication.getName().equals(changePasswordDto.username())){
            AuthnException exception = new AuthnException("You do not have the necessary permissions to access this resource.");
            exception.setCode(403);
            throw exception;
        }
        User user = findByUsername(changePasswordDto.username());

        if(!passwordEncoder.matches(changePasswordDto.oldPassword(), user.getPassword())){
            AuthnException authnException = new AuthnException("Bad Credentials");
            authnException.setCode(HttpStatus.UNAUTHORIZED.value());
            throw authnException;
        }
        validatePassword(changePasswordDto.newPassword());
        user.setPassword(passwordEncoder.encode(changePasswordDto.newPassword()));
        return userRepository.save(user);
    }

    @Override
    public String changeActivityStatus(String username, ToggleStatusDto statusDto) throws EntityException, ValidateException {
        if (!username.equals(statusDto.username())) {
            throw new ValidateException("Username should be the same");
        }
        User user = findByUsername(username);
        user.setIsActive(statusDto.isActive()); //status changes here
        user = userRepository.save(user);
        String status = user.getIsActive() ? " is active" : " is inactive";
        return username + status;
    }

    /**
     * Method generate unique username based on provided first name and last name.
     * If current username already exists in database method adds digit to end of username and check again.
     * example 'john.doe1' if 'john.doe' is already exist
     *
     * @param firstName - first name of user
     * @param lastName  - last name of user
     * @return unique username for current database
     */
    private String generateUsername(String firstName, String lastName) {
        int count = 1;
        String template = firstName.toLowerCase() + "." + lastName.toLowerCase();
        String username = template;
        while (isUsernameExist(username)) {
            username = template + count;
            count++;
        }
        return username;
    }

    /**
     * checks if username exist in database
     *
     * @param username target username
     * @return true is username exist, otherwise false
     */
    private boolean isUsernameExist(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private void validatePassword(String password) {
        // todo check contains character and so on
    }

    @Override
    @Transactional
    public UserCredentials saveTrainee(TraineeDto traineeDto) throws GymException {
        String password = PasswordGenerator.generatePassword();
        User user = saveUser(traineeDto.getFirstName(), traineeDto.getLastName(), password);

        TraineeFullDto fullDto = TraineeMapper.map(traineeDto, user);

        log.info("try to save in another service");
        try{
            // call to fit-coach MS using feign client throws exception if failed
            fitCoachClient.saveTrainee(fullDto);
        } catch (FeignException e) {
            log.error("Failed to save trainee details in fit-coach microservice with status: ", e);
            throw new GymException("Internal error occurred while communicating with another microservice");
        }

        log.info("details saved");
        return new UserCredentials(user.getUsername(), password);
    }

    @Override
    public UserCredentials saveTrainer(TrainerDto trainerDto) throws GymException {
        String password = PasswordGenerator.generatePassword();
        User user = saveUser(trainerDto.getFirstName(), trainerDto.getLastName(), password);

        TrainerFullDto fullDto = TrainerMapper.map(trainerDto, user);
        log.info("try to save in another service");
        // call to fit-coach MS using feign client throws exception if failed
        try {
            fitCoachClient.saveTrainer(fullDto);
        } catch (FeignException e) {
            // Parse the status code from the FeignException
            int status = e.status();
            // Log the error
            log.error("Failed to save trainer details in fit-coach microservice with status: " + status, e);
            // Handle specific status codes
            if (status == HttpStatus.NOT_FOUND.value()) {
                // Handle 404 Not Found
                throw new EntityException("Could not find specialization with id: " + trainerDto.getSpecialization());
            } else {
                // Handle 500 Internal Server Error
                throw new GymException("Internal error occurred while communicating with another microservice");
            }
        }
        return new UserCredentials(user.getUsername(), password);
    }


    private User saveUser(String firstname, String lastname, String password) {
        String username = generateUsername(firstname, lastname);

        User user = new User();
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setIsActive(true);
        return userRepository.save(user);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> {
                    return new org.springframework.security.core.userdetails.User(
                            user.getUsername(),
                            user.getPassword(),
                            user.getRoles());  // Pass GrantedAuthorities to UserDetails
                })
                .orElseThrow(() -> new UsernameNotFoundException("Failed to retrieve user: " + username));
    }
}
