package com.krasnopolskyi.security.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,
    TRAINEE,
    TRAINER,
    SERVICE,
    ;

    @Override
    public String getAuthority() {
        return name();
    }
}
