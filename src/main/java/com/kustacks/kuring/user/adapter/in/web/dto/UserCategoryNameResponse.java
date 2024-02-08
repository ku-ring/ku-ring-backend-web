package com.kustacks.kuring.user.adapter.in.web.dto;

import com.kustacks.kuring.user.application.port.in.dto.UserCategoryNameResult;

public record UserCategoryNameResponse(
        String name,
        String hostPrefix,
        String korName
) {
    public static UserCategoryNameResponse from(UserCategoryNameResult name) {
        return new UserCategoryNameResponse(name.name(), name.hostPrefix(), name.korName());
    }
}
