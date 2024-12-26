package com.krasnopolskyi.fitcoach.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private Boolean isActive;
}
