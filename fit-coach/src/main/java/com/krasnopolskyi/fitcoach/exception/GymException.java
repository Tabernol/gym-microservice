package com.krasnopolskyi.fitcoach.exception;

public class GymException extends Exception{

    public GymException(String message) {
        super(message);
    }

    public GymException(String message, Throwable cause) {
        super(message, cause);
    }
}