package com.krasnopolskyi.fitcoach.service;

import com.krasnopolskyi.fitcoach.dto.request.*;
import com.krasnopolskyi.fitcoach.dto.response.TrainerProfileDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainingResponseDto;
import com.krasnopolskyi.fitcoach.dto.response.UserDto;
import com.krasnopolskyi.fitcoach.entity.Role;
import com.krasnopolskyi.fitcoach.entity.Trainer;
import com.krasnopolskyi.fitcoach.entity.TrainingType;
import com.krasnopolskyi.fitcoach.entity.User;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.GymException;
import com.krasnopolskyi.fitcoach.exception.ValidateException;
import com.krasnopolskyi.fitcoach.repository.TrainerRepository;
import com.krasnopolskyi.fitcoach.utils.mapper.TrainerMapper;
import com.krasnopolskyi.fitcoach.utils.password_generator.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final UserService userService;
    private final TrainingTypeService trainingTypeService;
    private final TrainingService trainingService;

    @Transactional
    public UserCredentials save(TrainerDto trainerDto) throws EntityException {
        String password = PasswordGenerator.generatePassword();
        TrainingType specialization = trainingTypeService.findById(trainerDto.getSpecialization()); // receive specialization

        User newUser = userService
                .create(new UserDto(trainerDto.getFirstName(),
                        trainerDto.getLastName(), password)); //return user with firstName, lastName, username, hashedPassword, isActive
        newUser.getRoles().add(Role.TRAINER); // adds role
        Trainer trainer = new Trainer();
        trainer.setUser(newUser);
        trainer.setSpecialization(specialization);

        Trainer saveTrainer = trainerRepository.save(trainer);// save entity
        return new UserCredentials(saveTrainer.getUser().getUsername(), password);
    }

    @Transactional(readOnly = true)
    public TrainerProfileDto findByUsername(String username) throws EntityException {
        return trainerRepository.findByUsername(username)
                .map(trainer -> TrainerMapper.mapToDto(trainer))
                .orElseThrow(() -> new EntityException("Can't find trainer with username " + username));
    }


    public List<TrainingResponseDto> getTrainings(TrainingFilterDto filter) throws EntityException {
        getByUsername(filter.getOwner()); // validate if exist trainer with such username
        return trainingService.getFilteredTrainings(filter);
    }


    @Transactional
    public TrainerProfileDto update(String username, TrainerUpdateDto trainerDto) throws GymException {
        if(!username.equals(trainerDto.username())){
            throw new ValidateException("Username should be the same");
        }
        Trainer trainer = getByUsername(trainerDto.username());
        //update user's fields
        User user = trainer.getUser();
        user.setFirstName(trainerDto.firstName());
        user.setLastName(trainerDto.lastName());
        user.setIsActive(trainerDto.isActive());

        Trainer savedTrainer = trainerRepository.save(trainer); // pass refreshed trainer to repository
        return TrainerMapper.mapToDto(savedTrainer);
    }


    @Transactional
    public String changeStatus(String username, ToggleStatusDto statusDto) throws EntityException, ValidateException {
        //here or above need check if current user have permissions to change trainee
        if(!username.equals(statusDto.username())){
            throw new ValidateException("Username should be the same");
        }
        Trainer trainer = getByUsername(statusDto.username()); // validate is trainer exist with this name
        User user = userService.changeActivityStatus(statusDto);
        String result = "Status of trainer " + user.getUsername() + " is " + (user.getIsActive() ? "activated": "deactivated");
        return result;
    }

    private Trainer getByUsername(String username) throws EntityException {
        return trainerRepository.findByUsername(username)
                .orElseThrow(() -> new EntityException("Can't find trainer with username " + username));
    }
}
