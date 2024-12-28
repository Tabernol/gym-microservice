package com.krasnopolskyi.report.model;

import lombok.Data;

import java.util.List;
@Data
public class ReportTraining {
    private Trainer trainer;
    private List<YearTrainingData> report;
}
