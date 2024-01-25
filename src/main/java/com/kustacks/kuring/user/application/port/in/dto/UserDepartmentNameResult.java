package com.kustacks.kuring.user.application.port.in.dto;

import com.kustacks.kuring.notice.domain.DepartmentName;

public record UserDepartmentNameResult(
        String name,
        String hostPrefix,
        String korName
) {
    public static UserDepartmentNameResult from(DepartmentName name) {
        return new UserDepartmentNameResult(name.getName(), name.getHostPrefix(), name.getKorName());
    }
}
