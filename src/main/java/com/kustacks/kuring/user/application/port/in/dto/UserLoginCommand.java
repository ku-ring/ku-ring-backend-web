package com.kustacks.kuring.user.application.port.in.dto;

public record UserLoginCommand(
        String fcmToken,
        String email,
        String password
) {
}
