package com.kustacks.kuring.notice.application.port.in.dto;

import com.kustacks.kuring.notice.domain.CategoryName;

public record NoticeCategoryNameResult(
        String name,
        String hostPrefix,
        String korName
) {
    public static NoticeCategoryNameResult from(CategoryName name) {
        return new NoticeCategoryNameResult(name.getName(), name.getShortName(), name.getKorName());
    }
}
