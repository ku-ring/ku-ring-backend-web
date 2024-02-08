package com.kustacks.kuring.notice.adapter.in.web.dto;

import com.kustacks.kuring.notice.application.port.in.dto.NoticeDepartmentNameResult;

public record NoticeDepartmentNameResponse(
        String name,
        String hostPrefix,
        String korName
) {
    public static NoticeDepartmentNameResponse from(NoticeDepartmentNameResult result) {
        return new NoticeDepartmentNameResponse(
                result.name(),
                result.hostPrefix(),
                result.korName()
        );
    }
}
