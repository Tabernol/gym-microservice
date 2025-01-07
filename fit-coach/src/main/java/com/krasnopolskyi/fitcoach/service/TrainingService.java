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
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.GymException;
import com.krasnopolskyi.fitcoach.exception.ValidateException;
import com.krasnopolskyi.fitcoach.repository.*;
import com.krasnopolskyi.fitcoach.utils.mapper.TrainingMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
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
    private final JmsTemplate jmsTemplate;

    @Transactional
    public TrainingResponseDto save(TrainingDto trainingDto) throws GymException {
        // todo who can add training ? currently any authenticated user can add any users to training and save

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

        // call to report MS using activeMQ
        jmsTemplate.convertAndSend("training.session", trainingSessionDto, message -> {
            message.setStringProperty("_typeId_", "training.session");
            return message;
        });

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

    @Transactional
    public boolean delete(long id) throws GymException {
        // todo check permission for action who can delete ?
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new EntityException("Could not find training: " + id));

        TrainingSessionDto trainingSessionDto = TrainingMapper.mapToDto(
                training,
                training.getTrainer(),
                TrainingSessionOperation.DELETE);

        // call to report MS using activeMQ
        jmsTemplate.convertAndSend("training.session", trainingSessionDto, message -> {
            message.setStringProperty("_typeId_", "training.session");
            return message;
        });


        return trainingRepository.findById(id)
                .map(entity -> {
                    trainingRepository.delete(entity);
                    trainingRepository.flush();
                    return true;
                })
                .orElse(false);
    }
}
