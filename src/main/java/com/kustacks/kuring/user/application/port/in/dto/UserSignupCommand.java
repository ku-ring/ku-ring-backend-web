package com.kustacks.kuring.user.application.port.in.dto;

public record UserSignupCommand(
    String email,
    String password
) {
}
