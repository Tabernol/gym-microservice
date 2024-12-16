package com.krasnopolskyi.fitcoach.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Setter
//@ToString(exclude = "password")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
//    private String password;
    private Boolean isActive;

//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
//    @Enumerated(EnumType.STRING)
//    @Column(name = "role")
//    private Set<Role> roles = new HashSet<>();
}
