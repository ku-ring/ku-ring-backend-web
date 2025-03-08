package com.kustacks.kuring.user.adapter.in.web.dto;

public record UserSignupRequest(
        String email,
        String password
) {
}
