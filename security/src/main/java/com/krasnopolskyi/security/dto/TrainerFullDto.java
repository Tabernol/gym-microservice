package com.krasnopolskyi.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class TrainerFullDto implements Serializable {
    private long userId;
    private String firstName;
    private String lastName;
    private String username;
    private Boolean isActive;
    private Integer specialization;
}
