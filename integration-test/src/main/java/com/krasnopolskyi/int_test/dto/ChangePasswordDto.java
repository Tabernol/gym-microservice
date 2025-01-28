package com.krasnopolskyi.int_test.dto;


public record ChangePasswordDto(
        String username,
        String oldPassword,
        String newPassword) {
}
