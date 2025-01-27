package com.krasnopolskyi.int_test.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TrainerDto {
    private String firstName;
    private String lastName;
    private Integer specialization;
}
