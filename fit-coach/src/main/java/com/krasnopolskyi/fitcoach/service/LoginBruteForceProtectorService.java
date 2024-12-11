package com.krasnopolskyi.fitcoach.service;

import com.krasnopolskyi.fitcoach.exception.AuthnException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
@Slf4j
public class LoginBruteForceProtectorService {
    private final int MAX_ATTEMPT = 3;
    private final long BLOCK_TIME_MS = 5 * 60 * 1000; // 5 minutes
    private final Map<String, LoginAttempt> attemptsCache = new ConcurrentHashMap<>();


    public void runBruteForceProtector(String username) {
        LoginAttempt loginAttempt = attemptsCache
                .getOrDefault(username, new LoginAttempt(0, System.currentTimeMillis()));
        loginAttempt.incrementAttempts();
        attemptsCache.put(username, loginAttempt);
    }

    public void isBlocked(String username) throws AuthnException {
        LoginAttempt loginAttempt = attemptsCache.get(username);

        if (loginAttempt == null) {
            return;  // No block.
        }

        if (loginAttempt.getAttempts() >= MAX_ATTEMPT) {
            long lastAttemptTime = loginAttempt.getLastAttemptTime();
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastAttemptTime < BLOCK_TIME_MS) {
                log.warn("User: " + username + ". has been blocked for a 5 minutes");
                AuthnException authnException = new AuthnException("Please wait " +
                        (((lastAttemptTime + BLOCK_TIME_MS) - currentTime)/1000) + " seconds before trying again");
                authnException.setCode(HttpStatus.FORBIDDEN.value());
                throw authnException;
            } else {
                log.info("User: " + username + ". has been removed from blacklist");
                attemptsCache.remove(username);  // Unblock after block time is over.
            }
        }
    }


    @AllArgsConstructor
    @Getter
    private static class LoginAttempt {
        private int attempts;
        private long lastAttemptTime;

        public void incrementAttempts() {
            this.attempts++;
            this.lastAttemptTime = System.currentTimeMillis();
        }
    }
}
