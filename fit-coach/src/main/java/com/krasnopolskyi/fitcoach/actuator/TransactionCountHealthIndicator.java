package com.krasnopolskyi.fitcoach.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class TransactionCountHealthIndicator implements HealthIndicator {
    private static final String LOG_DIRECTORY_PATH = "log";

    // passes to actuator number of transaction today
    @Override
    public Health health() {

        try {
            String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String transactionLogPath = LOG_DIRECTORY_PATH + "/" + todayDate + "/transaction.log";

            // Read all lines from the transaction log file
            Path path = Path.of(transactionLogPath);
            long lineCount = Files.lines(path).count();

            // Calculate the number of transactions each transaction has 2 lines: start and result
            long transactionCount = lineCount / 2;

            long errorCount = Files.lines(path)
                    .filter(line -> line.contains("ERROR"))
                    .count();

            // Return health status with transaction count as a detail
            return Health.up()
                    .withDetail("date", todayDate)
                    .withDetail("transactionsToday", transactionCount)
                    .withDetail("errorCount", errorCount)
                    .build();
        } catch (IOException e) {
            // If there's an error (e.g., file not found), return DOWN status
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
