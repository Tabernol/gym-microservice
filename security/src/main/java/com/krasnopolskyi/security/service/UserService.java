package com.krasnopolskyi.security.service;


import com.krasnopolskyi.security.dto.TraineeDto;
import com.krasnopolskyi.security.dto.TrainerDto;
import com.krasnopolskyi.security.dto.UserCredentials;
import com.krasnopolskyi.security.entity.User;


//public interface UserService extends UserDetailsService {
public interface UserService {

    UserCredentials saveTrainee(TraineeDto traineeDto);
    UserCredentials saveTrainer(TrainerDto trainerDto);
//    User create(UserDto userDto);
//    User changePassword(ChangePasswordDto changePasswordDto) throws EntityException, AuthnException;
//    User changeActivityStatus(ToggleStatusDto statusDto) throws EntityException;
}
