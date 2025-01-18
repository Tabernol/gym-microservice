package com.krasnopolskyi.report.service;

import com.krasnopolskyi.report.entity.ReportTrainer;
import com.krasnopolskyi.report.entity.TrainingSession;
import com.krasnopolskyi.report.entity.TrainingSessionOperation;
import com.krasnopolskyi.report.model.*;
import com.krasnopolskyi.report.repository.ReportTrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final ReportTrainerRepository reportTrainerRepository;

    public ReportTrainer getReportByUsername(String username) {
        return reportTrainerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Could not found trainer with " + username));
    }

    // Method to save or update the ReportTrainer document
    public void saveOrUpdateReport(TrainingSession trainingSession) {
        Optional<ReportTrainer> existingReportTrainer = reportTrainerRepository.findByUsername(trainingSession.getUsername());

        if (existingReportTrainer.isPresent()) {
            // Trainer exists, so update the existing record
            ReportTrainer existingTrainer = existingReportTrainer.get();
            updateReport(existingTrainer, trainingSession); // update training fields
        } else {
            // Trainer does not exist, create a new ReportTrainer
            createNewReportTrainer(trainingSession);
        }
    }

    public ReportTrainer updateTrainer(Trainer trainer) {
        String username = trainer.getUsername();
        // Fetch the corresponding ReportTrainer entity from MongoDB
        Optional<ReportTrainer> existingTrainerOpt = reportTrainerRepository.findByUsername(username);

        if (existingTrainerOpt.isPresent()) {
            // Update the fields in the ReportTrainer entity with the data from the event
            ReportTrainer existingTrainer = existingTrainerOpt.get();
            existingTrainer.setFirstName(trainer.getFirstName());
            existingTrainer.setLastName(trainer.getLastName());
            existingTrainer.setActive(trainer.isActive());

            // Save the updated entity in MongoDB
            return reportTrainerRepository.save(existingTrainer);
        }
        return null;
    }

    private ReportTrainer createNewReportTrainer(TrainingSession trainingSession) {
        // Create a new ReportTrainer entity
        ReportTrainer reportTrainer = new ReportTrainer(
                null, // Let MongoDB generate the ID
                trainingSession.getUsername(),
                trainingSession.getFirstName(),
                trainingSession.getLastName(),
                trainingSession.isActive(),
                List.of(createYearTrainingData(trainingSession)) // Initialize with the current year training data
        );
        return reportTrainerRepository.save(reportTrainer);
    }

    private ReportTrainer.YearTrainingData createYearTrainingData(TrainingSession trainingSession) {
        // Create initial YearTrainingData with the training session
        ReportTrainer.SingleTrainingData singleTrainingData = createTrainingSession(trainingSession);

        ReportTrainer.MonthTrainingData monthTrainingData = new ReportTrainer.MonthTrainingData(
                trainingSession.getDate().getMonth(),
                List.of(singleTrainingData), // Initial session for the month
                trainingSession.getDuration() // Set initial month summary duration
        );

        return new ReportTrainer.YearTrainingData(
                trainingSession.getDate().getYear(), // Year
                List.of(monthTrainingData) // Initial month data
        );
    }

    private void updateReport(ReportTrainer existingTrainer, TrainingSession trainingSession) {
        // Update the existing trainer's data, add new training session to the appropriate year/month
        int year = trainingSession.getDate().getYear();

        // Check if the year already exists
        Optional<ReportTrainer.YearTrainingData> yearDataOpt = existingTrainer.getYearsData().stream()
                .filter(yearData -> yearData.getYear() == year)
                .findFirst();

        if (yearDataOpt.isPresent()) {
            // Year exists, update month data
            ReportTrainer.YearTrainingData yearData = yearDataOpt.get();
            updateMonthData(yearData, trainingSession);
        } else {
            // Year does not exist, create a new YearTrainingData
            ReportTrainer.YearTrainingData newYearData = createYearTrainingData(trainingSession);
            existingTrainer.getYearsData().add(newYearData);
        }

        // Sort YearTrainingData by year
        existingTrainer.getYearsData().sort(Comparator.comparingInt(ReportTrainer.YearTrainingData::getYear));

        // Save the updated trainer
        reportTrainerRepository.save(existingTrainer);
    }

    private void updateMonthData(ReportTrainer.YearTrainingData yearData, TrainingSession trainingSession) {
        // Find the correct month to add the training session
        Month month = trainingSession.getDate().getMonth();
        ReportTrainer.SingleTrainingData trainingData = createTrainingSession(trainingSession); // create training session

        // Check if the year already exists
        Optional<ReportTrainer.MonthTrainingData> monthTrainingData = yearData.getMonthsData().stream()
                .filter(monthData -> monthData.getMonth() == month)
                .findFirst();

        if (monthTrainingData.isPresent()) {
            // month exists
            ReportTrainer.MonthTrainingData monthData = monthTrainingData.get();

            if(trainingSession.getOperation() == TrainingSessionOperation.DELETE){
               // set training operation DELETE if present
                monthData.getTrainingSessions().stream()
                        .filter(session -> session.getSessionId() == trainingData.getSessionId())
                        .findFirst()
                        .ifPresent(session -> session.setOperation(TrainingSessionOperation.DELETE));
            } else {
                monthData.getTrainingSessions().add(trainingData);
                // calculate summary month duration
                monthData.setMonthSummaryDuration(monthTrainingData.get().getMonthSummaryDuration() + trainingSession.getDuration());

                // sort training by date of session
                monthData.getTrainingSessions().sort(Comparator.comparing(ReportTrainer.SingleTrainingData::getDate));
            }
        } else {

            ReportTrainer.MonthTrainingData data = new ReportTrainer.MonthTrainingData(
                    trainingSession.getDate().getMonth(),
                    List.of(trainingData),
                    trainingData.getDuration());

            yearData.getMonthsData().add(data);

            // sort by month
            yearData.getMonthsData().sort(Comparator.comparing(ReportTrainer.MonthTrainingData::getMonth));
        }
    }

    private ReportTrainer.SingleTrainingData createTrainingSession(TrainingSession trainingSession) {
        return new ReportTrainer.SingleTrainingData
                (trainingSession.getId(),
                        trainingSession.getDate(),
                        trainingSession.getDuration(),
                        trainingSession.getOperation());
    }
}
