package com.kustacks.kuring.notice.adapter.in.web.dto;

import com.kustacks.kuring.notice.application.port.in.dto.NoticeCategoryNameResult;

public record NoticeCategoryNameResponse(
        String name,
        String hostPrefix,
        String korName
) {
    public static NoticeCategoryNameResponse from(NoticeCategoryNameResult result) {
        return new NoticeCategoryNameResponse(
                result.name(),
                result.hostPrefix(),
                result.korName()
        );
    }
}
