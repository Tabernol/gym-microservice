package com.krasnopolskyi.report.model;


import java.time.LocalDate;


public record SingleTrainingData(long id, LocalDate date, int duration) {
}
