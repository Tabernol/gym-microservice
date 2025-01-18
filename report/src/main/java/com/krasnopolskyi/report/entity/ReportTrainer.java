package com.krasnopolskyi.report.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Data
@AllArgsConstructor
@Document(collection = "report-trainers")  // MongoDB collection name
public class ReportTrainer {
    @Id
    private String id;

    @Indexed(unique = true, sparse = true)
    private String username;
    private String firstName;
    private String lastName;
    private boolean isActive;

    private List<YearTrainingData> yearsData;

    @Data
    @AllArgsConstructor
    public static class YearTrainingData {
        private int year;
        private List<MonthTrainingData> monthsData;
    }

    @Data
    @AllArgsConstructor
    public static class MonthTrainingData {
        private Month month;
        private List<SingleTrainingData> trainingSessions;
        private int monthSummaryDuration;
    }

    @Data
    @AllArgsConstructor
    public static class SingleTrainingData {
        private long sessionId;
        private LocalDate date;
        private int duration;
        private TrainingSessionOperation operation;
    }
}
