package com.krasnopolskyi.security.service;


import com.krasnopolskyi.security.dto.*;
import com.krasnopolskyi.security.entity.User;
import com.krasnopolskyi.security.exception.AuthnException;
import com.krasnopolskyi.security.exception.EntityException;
import com.krasnopolskyi.security.exception.GymException;
import com.krasnopolskyi.security.exception.ValidateException;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserCredentials saveTrainee(TraineeDto traineeDto) throws GymException;

    UserCredentials saveTrainer(TrainerDto trainerDto) throws EntityException, GymException;

    User changePassword(ChangePasswordDto changePasswordDto) throws EntityException, AuthnException;

    String changeActivityStatus(String username, ToggleStatusDto statusDto) throws EntityException, ValidateException;
}
