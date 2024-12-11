package com.krasnopolskyi.fitcoach.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,
    TRAINEE,
    TRAINER,
    ;

    @Override
    public String getAuthority() {
        return name();
    }
}
