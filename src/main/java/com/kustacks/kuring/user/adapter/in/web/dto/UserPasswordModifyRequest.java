package com.kustacks.kuring.user.adapter.in.web.dto;

public record UserPasswordModifyRequest(
        String email,
        String password
) {
}
