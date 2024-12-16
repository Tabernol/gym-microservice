package com.krasnopolskyi.security.dto.event;

import com.krasnopolskyi.security.dto.TraineeFullDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RegisterTraineeEvent extends ApplicationEvent {
    private final TraineeFullDto traineeFullDto;

    public RegisterTraineeEvent(Object source, TraineeFullDto traineeFullDto) {
        super(source);
        this.traineeFullDto = traineeFullDto;
    }
}
