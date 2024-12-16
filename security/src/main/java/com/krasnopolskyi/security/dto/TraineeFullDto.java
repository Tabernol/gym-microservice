package com.krasnopolskyi.security.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class TraineeFullDto {
    private long userId;
    private String firstName;
    private String lastName;
    private String username;
    private Boolean isActive;
    private LocalDate dateOfBirth;
    private String address;
}
