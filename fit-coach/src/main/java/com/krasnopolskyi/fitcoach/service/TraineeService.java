package com.krasnopolskyi.fitcoach.service;


import com.krasnopolskyi.fitcoach.dto.request.*;
import com.krasnopolskyi.fitcoach.dto.response.TraineeProfileDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainerProfileShortDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainingResponseDto;
import com.krasnopolskyi.fitcoach.dto.response.UserDto;
import com.krasnopolskyi.fitcoach.entity.Role;
import com.krasnopolskyi.fitcoach.entity.Trainee;
import com.krasnopolskyi.fitcoach.entity.Trainer;
import com.krasnopolskyi.fitcoach.entity.User;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.ValidateException;
import com.krasnopolskyi.fitcoach.repository.TraineeRepository;
import com.krasnopolskyi.fitcoach.repository.TrainerRepository;
import com.krasnopolskyi.fitcoach.utils.mapper.TraineeMapper;
import com.krasnopolskyi.fitcoach.utils.mapper.TrainerMapper;
import com.krasnopolskyi.fitcoach.utils.password_generator.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TraineeService {
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final UserService userService;
    private final TrainingService trainingService;

    @Transactional
    public UserCredentials save(TraineeDto traineeDto) {
        String password = PasswordGenerator.generatePassword();
        User newUser = userService
                .create(new UserDto(traineeDto.getFirstName(),
                        traineeDto.getLastName(), password)); //return user with firstName, lastName, username, hashedPassword, isActive

        newUser.getRoles().add(Role.TRAINEE); // adds role
        Trainee trainee = TraineeMapper.mapToEntity(traineeDto, newUser);

        Trainee savedTrainee = traineeRepository.save(trainee);// pass to repository
        log.debug("trainee has been saved " + trainee);
        return new UserCredentials(savedTrainee.getUser().getUsername(), password);
    }

    @Transactional(readOnly = true) //generate test
    public TraineeProfileDto findByUsername(String username) throws EntityException {
        return TraineeMapper.mapToDto(getByUsername(username));
    }

    @Transactional
    public TraineeProfileDto update(String username, TraineeUpdateDto traineeDto) throws EntityException, ValidateException {
        if(!username.equals(traineeDto.username())){
            throw new ValidateException("Username should be the same");
        }
        // find trainee entity
        Trainee trainee = getByUsername(traineeDto.username());
        //update trainee's fields
        trainee.setAddress(traineeDto.address());
        trainee.setDateOfBirth(traineeDto.dateOfBirth());

        //update user's fields
        User user = trainee.getUser();
        user.setFirstName(traineeDto.firstName());
        user.setLastName(traineeDto.lastName());
        user.setIsActive(traineeDto.isActive());

        Trainee savedTrainee = traineeRepository.save(trainee);
        log.debug("trainee has been updated " + trainee.getId());
        return TraineeMapper.mapToDto(savedTrainee);
    }

    @Transactional
    public boolean delete(String username) {
        return traineeRepository.findByUsername(username)
                .map(entity -> {
                    traineeRepository.delete(entity);
                    traineeRepository.flush();
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public List<TrainerProfileShortDto> updateTrainers(String username, List<String> trainerUsernames) throws EntityException {
        Trainee trainee = getByUsername(username);

        trainee.getTrainers().clear();
        for (String trainerUsername : trainerUsernames) {
            Trainer trainer = trainerRepository.findByUsername(trainerUsername)
                    .orElseThrow(() -> new EntityException("Could not found trainer with id " + trainerUsername));
            trainer.getTrainees().add(trainee);
            trainee.getTrainers().add(trainer);
        }

        return trainee.getTrainers()
                .stream()
                .map(TrainerMapper::mapToShortDto)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<TrainerProfileShortDto> findAllNotAssignedTrainersByTrainee(String username) throws EntityException {
        Trainee trainee = getByUsername(username);

        List<Trainer> allTrainers = trainerRepository.findAllActiveTrainers();
        allTrainers.removeAll(trainee.getTrainers());
        return allTrainers.stream().map(TrainerMapper::mapToShortDto).toList();
    }

    public List<TrainingResponseDto> getTrainings(TrainingFilterDto filter) throws EntityException {
        getByUsername(filter.getOwner()); // validate if exist trainee with such username
        return trainingService.getFilteredTrainings(filter);
    }

    @Transactional
    public String changeStatus(String username, ToggleStatusDto statusDto) throws ValidateException, EntityException {
        //here or above need check if current user have permissions to change trainee
        if(!username.equals(statusDto.username())){
            throw new ValidateException("Username should be the same");
        }
        Trainee trainee = getByUsername(statusDto.username()); // validate is trainee exist with this name
        User user = userService.changeActivityStatus(statusDto);
        String result = "Status of trainee " + user.getUsername() + " is " + (user.getIsActive() ? "activated": "deactivated");
        return result;
    }

    private Trainee getByUsername(String username) throws EntityException {
        return traineeRepository.findByUsername(username)
                .orElseThrow(() -> new EntityException("Can't find trainee with username " + username));
    }

}
