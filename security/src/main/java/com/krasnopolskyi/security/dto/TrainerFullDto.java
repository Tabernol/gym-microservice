package com.krasnopolskyi.security.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TrainerFullDto {
    private long userId;
    private String firstName;
    private String lastName;
    private String username;
    private Boolean isActive;
    private Integer specialization;
}
