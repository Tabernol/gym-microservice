package com.krasnopolskyi.fitcoach.dto.event;

import com.krasnopolskyi.fitcoach.dto.request.training.TrainingSessionDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
public class TrainingSessionEvent extends ApplicationEvent {

    private final TrainingSessionDto trainingSessionDto;

    public TrainingSessionEvent(Object source, TrainingSessionDto trainingSessionDto) {
        super(source);
        this.trainingSessionDto = trainingSessionDto;
    }
}
