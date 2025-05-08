package com.kustacks.kuring.user.application.port.in.dto;

public record UserInfoResult(
        Long userId,
        String nickname,
        String email
) {
}
