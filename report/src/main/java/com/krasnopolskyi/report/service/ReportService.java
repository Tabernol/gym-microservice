package com.krasnopolskyi.report.service;

import com.krasnopolskyi.report.entity.TrainingSession;
import com.krasnopolskyi.report.entity.TrainingSessionOperation;
import com.krasnopolskyi.report.http.client.FitCoachClient;
import com.krasnopolskyi.report.model.*;
import com.krasnopolskyi.report.repository.TrainingSessionRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TrainingSessionRepository trainingSessionRepository;
    private final FitCoachClient fitCoachClient;

    public ReportTraining getReportByUsername(String username) {
        Trainer trainer;
        try {
            trainer = fitCoachClient.getTrainer(username).getBody();
        } catch (FeignException ex) {
            trainer = new Trainer(username, "Unknown", "Unknown", true);
        }

        return buildReportForUser(trainer);
    }

    private List<TrainingSession> getSessionsByUsername(String username) {
        List<TrainingSession> sessions = trainingSessionRepository
                .findAllByUsernameAndOperation(username, TrainingSessionOperation.ADD);
        if (sessions.isEmpty()) {
            return new ArrayList<>();
        }
        return sessions;
    }

    private ReportTraining buildReportForUser(Trainer trainer) {
        List<TrainingSession> sessions = getSessionsByUsername(trainer.getUsername());

        List<SingleTrainingData> trainingData = mapToSingleTrainingData(sessions);
        Map<Integer, Map<Month, List<SingleTrainingData>>> groupedData = groupDataByYearAndMonth(trainingData);

        List<YearTrainingData> yearTrainingDataList = groupedData.entrySet().stream()
                .map(entry -> buildYearTrainingData(entry.getKey(), entry.getValue()))
                .filter(yearData -> !yearData.getYearTrainingData().isEmpty()) // Only include years with data
                .collect(Collectors.toList());

        return createReport(trainer, yearTrainingDataList);
    }

    private List<SingleTrainingData> mapToSingleTrainingData(List<TrainingSession> sessions) {
        return sessions.stream()
                .map(session -> new SingleTrainingData(
                        session.getId(),
                        session.getDate(),
                        session.getDuration()))
                .collect(Collectors.toList());
    }

    private Map<Integer, Map<Month, List<SingleTrainingData>>> groupDataByYearAndMonth(List<SingleTrainingData> data) {
        return data.stream().collect(Collectors.groupingBy(
                d -> d.date().getYear(),
                Collectors.groupingBy(d -> d.date().getMonth())
        ));
    }

    private YearTrainingData buildYearTrainingData(Integer year, Map<Month, List<SingleTrainingData>> monthData) {
        List<MonthTrainingData> monthTrainingDataList = monthData.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty()) // Exclude empty months
                .map(entry -> buildMonthTrainingData(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        int yearDuration = monthTrainingDataList.stream()
                .mapToInt(MonthTrainingData::getMonthSummaryDuration)
                .sum();

        return new YearTrainingData(monthTrainingDataList, yearDuration);
    }

    private MonthTrainingData buildMonthTrainingData(Month month, List<SingleTrainingData> monthData) {
        int monthDuration = monthData.stream().mapToInt(SingleTrainingData::duration).sum();
        return new MonthTrainingData(monthData, monthDuration);
    }

    private ReportTraining createReport(Trainer trainer, List<YearTrainingData> yearData) {
        ReportTraining report = new ReportTraining();
        report.setTrainer(trainer);
        report.setReport(yearData);
        return report;
    }
}
