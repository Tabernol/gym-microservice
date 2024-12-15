package com.krasnopolskyi.report.model;

import lombok.Data;

import java.util.List;
@Data
public class ReportTraining {
    private String username;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private List<YearTrainingData> report;
}
