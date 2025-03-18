package com.kustacks.kuring.user.application.port.in.dto;

public record UserPasswordModifyCommand(
        String email,
        String password
) {
}
