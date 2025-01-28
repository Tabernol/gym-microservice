package com.krasnopolskyi.int_test.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class TraineeDto {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;
}
