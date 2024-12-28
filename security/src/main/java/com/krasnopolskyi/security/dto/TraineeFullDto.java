package com.krasnopolskyi.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class TraineeFullDto {
    private long userId;
    private String firstName;
    private String lastName;
    private String username;
    private Boolean isActive;
    private LocalDate dateOfBirth;
    private String address;
}
