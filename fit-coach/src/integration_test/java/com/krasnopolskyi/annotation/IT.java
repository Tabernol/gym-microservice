package com.krasnopolskyi.annotation;

import com.krasnopolskyi.fitcoach.FitCoachApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation uses for marking that it is integration test
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ActiveProfiles("test")
@Transactional
@SpringBootTest(classes = FitCoachApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public @interface IT {
}
