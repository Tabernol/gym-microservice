package com.krasnopolskyi.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class MonthTrainingData {
    private List<SingleTrainingData> monthTrainingData;
    private int monthSummaryDuration;
}
