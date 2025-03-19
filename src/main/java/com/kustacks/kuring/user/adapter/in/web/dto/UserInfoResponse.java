package com.kustacks.kuring.user.adapter.in.web.dto;

import com.kustacks.kuring.user.application.port.in.dto.UserInfoResult;

public record UserInfoResponse(
        String email,
        String nickname
) {
    public static UserInfoResponse from(UserInfoResult userInfoResult) {
        return new UserInfoResponse(
                userInfoResult.email(),
                userInfoResult.nickname()
        );
    }
}
