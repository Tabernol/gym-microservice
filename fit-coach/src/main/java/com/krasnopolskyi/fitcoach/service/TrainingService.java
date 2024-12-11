package com.krasnopolskyi.fitcoach.service;


import com.krasnopolskyi.fitcoach.dto.request.TrainingDto;
import com.krasnopolskyi.fitcoach.dto.request.TrainingFilterDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainingResponseDto;
import com.krasnopolskyi.fitcoach.entity.Trainee;
import com.krasnopolskyi.fitcoach.entity.Trainer;
import com.krasnopolskyi.fitcoach.entity.Training;
import com.krasnopolskyi.fitcoach.entity.User;
import com.krasnopolskyi.fitcoach.exception.AuthnException;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.ValidateException;
import com.krasnopolskyi.fitcoach.repository.*;
import com.krasnopolskyi.fitcoach.utils.mapper.TrainingMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainingService {
    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final MeterRegistry meterRegistry;

    @Transactional
    public TrainingResponseDto save(TrainingDto trainingDto) throws EntityException, ValidateException, AuthnException {
        validate(trainingDto);
        Trainee trainee = traineeRepository.findByUsername(trainingDto.getTraineeUsername())
                .orElseThrow(() -> new EntityException("Could not find trainee with " + trainingDto.getTraineeUsername()));

        Trainer trainer = trainerRepository.findByUsername(trainingDto.getTrainerUsername())
                .orElseThrow(() -> new EntityException("Could not find trainer with id " + trainingDto.getTrainerUsername()));

        isUserActive(trainee.getUser()); // validate if user active
        isUserActive(trainer.getUser()); // validate if user active

        trainer.getTrainees().add(trainee); // save into set and table trainer_trainee

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainer.getSpecialization());
        training.setDate(trainingDto.getDate());
        training.setDuration(trainingDto.getDuration());
        training.setTrainingName(trainingDto.getTrainingName());

        trainee.getTrainers().add(trainer);
        trainer.getTrainees().add(trainee);

        trainingRepository.save(training);

        return TrainingMapper.mapToDto(training);
    }

    private void isUserActive(User user) throws ValidateException {
        if (!user.getIsActive()) {
            throw new ValidateException("Profile " + user.getFirstName() + " " + user.getLastName() +
                    " is currently disabled");
        }
    }

    @Transactional(readOnly = true)
    public List<TrainingResponseDto> getFilteredTrainings(TrainingFilterDto filter) throws EntityException {
        userRepository.findByUsername(filter.getOwner())
                .orElseThrow(() -> new EntityException("Could not found user: " + filter.getOwner()));

        // start timer for find trainings and put to metrics
        Timer.Sample timer = Timer.start(meterRegistry);

        List<TrainingResponseDto> trainings = trainingRepository.getFilteredTrainings(
                        filter.getOwner(),
                        filter.getPartner(),
                        filter.getStartDate(),
                        filter.getEndDate(),
                        filter.getTrainingType()
                ).stream()
                .map(TrainingMapper::mapToDto)
                .toList();

        // stop timer
        timer.stop(Timer.builder("service_trainings_find")
                .description("trainings searching timer")
                .register(meterRegistry));

        return trainings;
    }

    private void validate(TrainingDto trainingDto) throws AuthnException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ensure authentication is present and valid
        if (authentication == null || authentication.getName() == null) {
            AuthnException exception = new AuthnException("Authentication information is missing.");
            exception.setCode(401);
            throw exception;
        }

        String authenticatedUser = authentication.getName();

        // Check if the authenticated user matches the trainee or trainer username
        if (!isUserAuthorized(authenticatedUser, trainingDto)) {
            AuthnException exception = new AuthnException("You do not have the necessary permissions to access this resource.");
            exception.setCode(403);
            throw exception;
        }
    }

    private boolean isUserAuthorized(String authenticatedUser, TrainingDto trainingDto) {
        return authenticatedUser.equals(trainingDto.getTraineeUsername()) ||
                authenticatedUser.equals(trainingDto.getTrainerUsername());
    }
}
