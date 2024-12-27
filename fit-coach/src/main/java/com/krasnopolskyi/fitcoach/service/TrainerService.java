package com.krasnopolskyi.fitcoach.service;

import com.krasnopolskyi.fitcoach.dto.request.trainer.TrainerDto;
import com.krasnopolskyi.fitcoach.dto.request.trainer.TrainerUpdateDto;
import com.krasnopolskyi.fitcoach.dto.request.training.TrainingFilterDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainerProfileDto;
import com.krasnopolskyi.fitcoach.dto.response.TrainingResponseDto;
import com.krasnopolskyi.fitcoach.entity.Trainer;
import com.krasnopolskyi.fitcoach.entity.TrainingType;
import com.krasnopolskyi.fitcoach.entity.User;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.GymException;
import com.krasnopolskyi.fitcoach.exception.ValidateException;
import com.krasnopolskyi.fitcoach.repository.TrainerRepository;
import com.krasnopolskyi.fitcoach.utils.mapper.TrainerMapper;
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
    private final TrainingTypeService trainingTypeService;
    private final TrainingService trainingService;
    private final UserService userService;

    @Transactional
    public Trainer save(TrainerDto trainerDto) throws EntityException {
        TrainingType specialization = trainingTypeService.findById(trainerDto.getSpecialization()); // receive specialization

        Trainer trainer = TrainerMapper.mapToEntity(trainerDto, specialization);
        return trainerRepository.save(trainer);
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
        userService.updateRemoteUser(user); // update user in security module
        return TrainerMapper.mapToDto(savedTrainer);
    }

    private Trainer getByUsername(String username) throws EntityException {
        return trainerRepository.findByUsername(username)
                .orElseThrow(() -> new EntityException("Can't find trainer with username " + username));
    }
}
