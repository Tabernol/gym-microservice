package com.krasnopolskyi.fitcoach.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RemoteServiceHealthIndicator implements HealthIndicator {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String REMOTE_SERVICE_URL = "https://campus.epam.ua/ua";

    // passes to actuator status of remoute server
    @Override
    public Health health() {
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(REMOTE_SERVICE_URL, String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return Health.up()
                        .withDetail("campus.epam.ua", "Available").build();
            } else {
                return Health.down()
                        .withDetail("campus.epam.ua", "Unavailable").build();
            }
        } catch (Exception e) {
            return Health.down(e).withDetail("campus.epam.ua", "Error").build();
        }
    }
}
