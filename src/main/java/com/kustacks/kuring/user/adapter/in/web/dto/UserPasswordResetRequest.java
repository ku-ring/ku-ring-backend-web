package com.kustacks.kuring.user.adapter.in.web.dto;

public record UserPasswordResetRequest(
        String email,
        String password
) {
}
