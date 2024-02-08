package com.kustacks.kuring.notice.application.port.in.dto;

import com.kustacks.kuring.notice.domain.DepartmentName;

public record NoticeDepartmentNameResult(
        String name,
        String hostPrefix,
        String korName
) {
    public static NoticeDepartmentNameResult from(DepartmentName name) {
        return new NoticeDepartmentNameResult(name.getName(), name.getHostPrefix(), name.getKorName());
    }
}
