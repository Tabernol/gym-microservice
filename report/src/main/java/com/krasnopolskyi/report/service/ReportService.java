package com.krasnopolskyi.report.service;

import com.krasnopolskyi.report.entity.TrainingSession;
import com.krasnopolskyi.report.model.MonthTrainingData;
import com.krasnopolskyi.report.model.ReportTraining;
import com.krasnopolskyi.report.model.SingleTrainingData;
import com.krasnopolskyi.report.model.YearTrainingData;
import com.krasnopolskyi.report.repository.TrainingSessionRepository;
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

    public ReportTraining getReportByUsername(String username) {
        List<TrainingSession> sessions = trainingSessionRepository.findAllByUsername(username);
        if (sessions.isEmpty()) {
            return null;
        }

        return buildReportForUser(username, sessions);
    }

    private ReportTraining buildReportForUser(String username, List<TrainingSession> sessions) {
        TrainingSession firstSession = sessions.get(0);

        List<SingleTrainingData> trainingData = mapToSingleTrainingData(sessions);
        Map<Integer, Map<Month, List<SingleTrainingData>>> groupedData = groupDataByYearAndMonth(trainingData);

        List<YearTrainingData> yearTrainingDataList = groupedData.entrySet().stream()
                .map(entry -> buildYearTrainingData(entry.getKey(), entry.getValue()))
                .filter(yearData -> !yearData.getYearTrainingData().isEmpty()) // Only include years with data
                .collect(Collectors.toList());

        return createReport(username, firstSession, yearTrainingDataList);
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

    private ReportTraining createReport(String username, TrainingSession session, List<YearTrainingData> yearData) {
        ReportTraining report = new ReportTraining();
        report.setUsername(username);
        report.setFirstName(session.getFirstName());
        report.setLastName(session.getLastName());
        report.setActive(session.isActive());
        report.setReport(yearData);
        return report;
    }
}
