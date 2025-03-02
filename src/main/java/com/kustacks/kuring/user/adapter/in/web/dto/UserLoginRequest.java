package com.kustacks.kuring.user.adapter.in.web.dto;

public record UserLoginRequest(
        String email,
        String password
) {
}
