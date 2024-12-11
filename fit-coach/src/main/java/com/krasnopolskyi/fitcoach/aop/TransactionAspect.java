package com.krasnopolskyi.fitcoach.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j(topic = "TRANSACTION")
public class TransactionAspect {

    /**
     * this method does logging before and after each method annotated @Transaction
     * also log files is saved in log/currentDate/transaction.log
     * @param joinPoint means as point of some action
     * @return target method invocation
     * @throws Throwable
     */
    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object logTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        String transactionId = UUID.randomUUID().toString(); // random id of transaction
        LocalDateTime startTime = LocalDateTime.now();

        // Get method name and arguments
        String methodName = joinPoint.getSignature().toShortString();
        Object[] methodArgs = joinPoint.getArgs(); // collects args from original method

        log.info("Transaction Start  : TransactionId={}, Method={}, Time={}, Args={},",
                transactionId, methodName, startTime, formatArguments(methodArgs));

        try {
            Object result = joinPoint.proceed(); // returns service logic to original method
            // logs after successfully transaction
            log.info("Transaction Success: TransactionId={}, Method={}, Time={}",
                    transactionId, methodName, LocalDateTime.now());
            return result;
        } catch (Exception e) {
            // logs after failed transaction
            log.error("Transaction Failed: TransactionId={}, Method={}, Time={}, Error={}",
                    transactionId, methodName, LocalDateTime.now(), e.getMessage());
            throw e;
        }
    }

    // Helper method to format method arguments into a readable string
    // perhaps it is not good idea to save confidential data in such way
    // but for improving practical skills can be useful
    private String formatArguments(Object[] args) {
        if (args == null || args.length == 0) {
            return "No arguments";
        }
        return Arrays.stream(args)
                .map(arg -> arg != null ? arg.toString() : "null")
                .collect(Collectors.joining(", "));
    }

}
