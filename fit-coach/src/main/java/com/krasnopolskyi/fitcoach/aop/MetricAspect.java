package com.krasnopolskyi.fitcoach.aop;

import com.krasnopolskyi.fitcoach.http.metric.TrackCountMetric;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class MetricAspect {
    private final MeterRegistry meterRegistry;

    /**
     * Save to metrics how many times annotated method calls
     * @param joinPoint this point does when annotation using
     * @param trackCountMetric  custom annotation for tracking how many times method calls
     * @return
     * @throws Throwable
     */
    @Around("@annotation(trackCountMetric)")
    public Object trackMetric(ProceedingJoinPoint joinPoint, TrackCountMetric trackCountMetric) throws Throwable {
        // Create the counter using the name specified in the annotation
        Counter counter = Counter.builder(trackCountMetric.name())
                .description(trackCountMetric.description())
                .register(meterRegistry);

        // Increment the counter before proceeding to the method
        counter.increment();

        // Proceed with the method execution
        return joinPoint.proceed();
    }
}
