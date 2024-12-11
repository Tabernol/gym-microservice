package com.krasnopolskyi.fitcoach.validation.annotation.impl;

import com.krasnopolskyi.fitcoach.validation.annotation.CustomValidAge;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class CustomAgeValidator implements ConstraintValidator<CustomValidAge, LocalDate> {
    private static final int MAX_AGE = 100;

    @Override
    public boolean isValid(LocalDate dateOfBirth, ConstraintValidatorContext context) {
        if (dateOfBirth == null) {
            return true; // considered valid if it's null
        }

        LocalDate today = LocalDate.now();

        // Check if the date is in the future
        if (dateOfBirth.isAfter(today)) {
            // Customize the error message for future dates
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Date of birth cannot be in the future.")
                    .addConstraintViolation();
            return false;
        }

        // Check if the person is older than 100 years
        int age = Period.between(dateOfBirth, today).getYears();
        if (age >= MAX_AGE) {
            // Customize the error message for exceeding the age limit
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("You are so many years old " + age +
                            ". Are you sure that you what to register? Please contact with support for free plan.")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
