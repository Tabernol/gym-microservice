package com.krasnopolskyi.fitcoach.http.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final int status;
    private final String message;
    private List<ErrorContent> errors;

    /**
     * Record representing detailed error content for a specific field.
     *
     * @param field   The name of the field associated with the error.
     * @param message The error message related to the field.
     */
    private record ErrorContent(String field, String message) {
    }

    /**
     * Adds detailed error content for a specific field to the errors list.
     *
     * @param field   The name of the field associated with the error.
     * @param message The error message related to the field.
     */
    public void addErrorContent(String field, String message) {
        if (Objects.isNull(errors)) {
            errors = new ArrayList<>();
        }
        errors.add(new ErrorContent(field, message));
    }
}
