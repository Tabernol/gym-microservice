package com.krasnopolskyi.fitcoach.dto.request.trainer;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDto {
    private long userId;
    private String firstName;
    private String lastName;
    private String username;
    private Boolean isActive;
    private Integer specialization;
}
