package com.kustacks.kuring.user.adapter.in.web.dto;

import com.kustacks.kuring.user.application.port.in.dto.UserLoginResult;

public record UserLoginResponse(
        String accessToken
) {
    public static UserLoginResponse from(UserLoginResult result) {
        return new UserLoginResponse(result.accessToken());
    }
}
