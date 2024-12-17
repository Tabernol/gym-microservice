package com.krasnopolskyi.gateway.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthnException extends GymException {
    private int code = 403; // default
    public AuthnException(String message) {
        super(message);
    }
}
