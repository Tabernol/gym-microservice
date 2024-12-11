package com.krasnopolskyi.fitcoach.validation.annotation.impl;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CustomAgeValidatorTest {
    private CustomAgeValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new CustomAgeValidator();
    }

    @Test
    void isValid_ShouldReturnTrue_WhenDateOfBirthIsNull() {
        // Act
        boolean result = validator.isValid(null, context);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValid_ShouldReturnFalse_WhenDateOfBirthIsInTheFuture() {
        // Arrange
        LocalDate futureDate = LocalDate.now().plusDays(1);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);

        // Act
        boolean result = validator.isValid(futureDate, context);

        // Assert
        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Date of birth cannot be in the future.");
    }

    @Test
    void isValid_ShouldReturnFalse_WhenAgeIsGreaterThan100() {
        // Arrange
        LocalDate dateOfBirth = LocalDate.now().minusYears(101);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);

        // Act
        boolean result = validator.isValid(dateOfBirth, context);

        // Assert
        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("You are so many years old 101. Are you sure that you what to register? Please contact with support for free plan.");
    }

    @Test
    void isValid_ShouldReturnTrue_WhenAgeIsValid() {
        // Arrange
        LocalDate validDateOfBirth = LocalDate.now().minusYears(30);

        // Act
        boolean result = validator.isValid(validDateOfBirth, context);

        // Assert
        assertTrue(result);
    }
}
