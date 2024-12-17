package com.krasnopolskyi.security.service;


import com.krasnopolskyi.security.dto.TraineeDto;
import com.krasnopolskyi.security.dto.TrainerDto;
import com.krasnopolskyi.security.dto.UserCredentials;
import com.krasnopolskyi.security.entity.User;
import com.krasnopolskyi.security.exception.EntityException;
import com.krasnopolskyi.security.exception.GymException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;


//public interface UserService extends UserDetailsService {
public interface UserService extends UserDetailsService {

    UserCredentials saveTrainee(TraineeDto traineeDto) throws GymException;
    UserCredentials saveTrainer(TrainerDto trainerDto) throws EntityException, GymException;
//    User create(UserDto userDto);
//    User changePassword(ChangePasswordDto changePasswordDto) throws EntityException, AuthnException;
//    User changeActivityStatus(ToggleStatusDto statusDto) throws EntityException;
}
