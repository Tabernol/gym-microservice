package com.krasnopolskyi.fitcoach.service;


import com.krasnopolskyi.fitcoach.dto.request.training.TrainingDto;
import com.krasnopolskyi.fitcoach.dto.request.training.TrainingFilterDto;
import com.krasnopolskyi.fitcoach.dto.request.training.TrainingSessionDto;
import com.krasnopolskyi.fitcoach.dto.request.training.TrainingSessionOperation;
import com.krasnopolskyi.fitcoach.dto.response.TrainingResponseDto;
import com.krasnopolskyi.fitcoach.entity.Trainee;
import com.krasnopolskyi.fitcoach.entity.Trainer;
import com.krasnopolskyi.fitcoach.entity.Training;
import com.krasnopolskyi.fitcoach.entity.User;
import com.krasnopolskyi.fitcoach.exception.AuthnException;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.GymException;
import com.krasnopolskyi.fitcoach.exception.ValidateException;
import com.krasnopolskyi.fitcoach.http.client.ReportClient;
import com.krasnopolskyi.fitcoach.repository.*;
import com.krasnopolskyi.fitcoach.utils.mapper.TrainingMapper;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final ReportClient reportClient;

    @Transactional
    @CircuitBreaker(name = "fitCoachService", fallbackMethod = "fallbackSave")
    public TrainingResponseDto save(TrainingDto trainingDto) throws GymException {
        // todo who can add training ?
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

        Training savedTraining = trainingRepository.save(training);

        TrainingSessionDto trainingSessionDto = TrainingMapper.mapToDto(
                savedTraining,
                trainer,
                TrainingSessionOperation.ADD);


        log.info("try to save in another service");
        try{
            // call to report MS using feign client throws exception if failed
            reportClient.saveTrainingSession(trainingSessionDto);
        } catch (FeignException e) {
            log.error("Failed to pass training session to report microservice: ", e);
            throw e;
        }

        return TrainingMapper.mapToDto(training);
    }

    // Fallback method
    public TrainingResponseDto fallbackSave(TrainingDto trainingDto, Throwable throwable) throws GymException {
        log.error("Fallback method called due to exception: ", throwable);

        if(throwable instanceof GymException){
            throw (GymException) throwable;
        }

        // Create a fallback response with default values (or any other desired behavior)
        return new TrainingResponseDto(
                null,               // id: Could return null or some fallback id
                "Training Unavailable",  // trainingName: You can provide a generic message
                "Unknown",          // trainingType: Another fallback value
                "Unknown Trainer",  // trainerFullName: Default text indicating no trainer
                "Unknown Trainee",  // traineeFullName: Default text indicating no trainee
                LocalDate.now(),    // date: Set to current date (or fallback date)
                0                   // duration: Default to 0 or another fallback value
        );
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

    @Transactional
    public boolean delete(long id) throws GymException {
        // todo check permission for action who can delete ?
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new EntityException("Could not find training: " + id));

        TrainingSessionDto trainingSessionDto = TrainingMapper.mapToDto(
                training,
                training.getTrainer(),
                TrainingSessionOperation.DELETE);

        log.info("try to save in another service");
        try{
            // call to report MS using feign client throws exception if failed
            reportClient.saveTrainingSession(trainingSessionDto);
        } catch (FeignException e) {
            log.error("Failed to pass training session to report microservice: ", e);
            throw new GymException("Internal error occurred while communicating with another microservice");
        }

        return trainingRepository.findById(id)
                .map(entity -> {
                    trainingRepository.delete(entity);
                    trainingRepository.flush();
                    return true;
                })
                .orElse(false);
    }

    private void validate(TrainingDto trainingDto) throws AuthnException {
        // todo refresh validating process
    }
}
