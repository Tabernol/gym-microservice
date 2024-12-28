package com.krasnopolskyi.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Trainer {
    private String username;
    private String firstName;
    private String lastName;
    private boolean active;
}
