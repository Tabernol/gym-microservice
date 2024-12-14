package com.krasnopolskyi.fitcoach.dto.event;

import com.krasnopolskyi.fitcoach.dto.request.training.TrainingSessionDto;
import org.springframework.context.ApplicationEvent;

public class TrainingSessionEvent extends ApplicationEvent {

    private final TrainingSessionDto trainingSessionDto;

    public TrainingSessionEvent(Object source, TrainingSessionDto trainingSessionDto) {
        super(source);
        this.trainingSessionDto = trainingSessionDto;
    }

    public TrainingSessionDto getTrainingSessionDto() {
        return trainingSessionDto;
    }
}
