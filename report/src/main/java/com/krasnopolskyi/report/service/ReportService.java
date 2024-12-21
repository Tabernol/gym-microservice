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

    public ReportTraining getReportByUsername(String username){
        List<TrainingSession> all = trainingSessionRepository.findAllByUsername(username);
        if(all.size() == 0){
            return null;
//            throw new IllegalArgumentException("There is no data for user: " + username);
        }
        TrainingSession training = all.get(0);


        List<SingleTrainingData> data = all.stream().map(trainingSession ->
                new SingleTrainingData(
                        trainingSession.getId(),
                        trainingSession.getDate(),
                        trainingSession.getDuration()))
                .collect(Collectors.toList());



        // Group data by year and month
        Map<Integer, Map<Month, List<SingleTrainingData>>> groupedByYearAndMonth = data.stream()
                .collect(Collectors.groupingBy(
                        d -> d.date().getYear(),  // Group by year
                        Collectors.groupingBy(d -> d.date().getMonth()))); // Then group by month

        // Create report for the user
        List<YearTrainingData> yearTrainingDataList = new ArrayList<>();

        for (Map.Entry<Integer, Map<Month, List<SingleTrainingData>>> yearEntry : groupedByYearAndMonth.entrySet()) {
            int yearDuration = 0;
            List<MonthTrainingData> monthTrainingDataList = new ArrayList<>();

            for (Map.Entry<Month, List<SingleTrainingData>> monthEntry : yearEntry.getValue().entrySet()) {
                List<SingleTrainingData> monthData = monthEntry.getValue();
                if (!monthData.isEmpty()) {  // Exclude empty months
                    int monthDuration = monthData.stream().mapToInt(SingleTrainingData::duration).sum();
                    yearDuration += monthDuration;

                    monthTrainingDataList.add(new MonthTrainingData(monthData, monthDuration));
                }
            }

            // Only include the year if it has training data
            if (!monthTrainingDataList.isEmpty()) {
                yearTrainingDataList.add(new YearTrainingData(monthTrainingDataList, yearDuration));
            }
        }

        // Return the final report
        ReportTraining report = new ReportTraining();
        report.setUsername(username);
        report.setFirstName(training.getFirstName());
        report.setLastName(training.getLastName());
        report.setActive(training.isActive());
        report.setReport(yearTrainingDataList);

        return report;
    }
}
