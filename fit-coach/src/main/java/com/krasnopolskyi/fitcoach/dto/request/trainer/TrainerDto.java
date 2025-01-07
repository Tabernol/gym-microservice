package com.krasnopolskyi.fitcoach.dto.request.trainer;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDto implements Serializable {
    private long userId;
    private String firstName;
    private String lastName;
    private String username;
    private Boolean isActive;
    private Integer specialization;
}
