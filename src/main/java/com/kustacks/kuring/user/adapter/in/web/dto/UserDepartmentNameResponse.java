package com.kustacks.kuring.user.adapter.in.web.dto;

import com.kustacks.kuring.user.application.port.in.dto.UserDepartmentNameResult;

public record UserDepartmentNameResponse(
        String name,
        String hostPrefix,
        String korName
) {
    public static UserDepartmentNameResponse from(UserDepartmentNameResult name) {
        return new UserDepartmentNameResponse(name.name(), name.hostPrefix(), name.korName());
    }
}
