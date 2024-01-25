package com.kustacks.kuring.user.application.port.in.dto;

import com.kustacks.kuring.notice.domain.CategoryName;

public record UserCategoryNameResult(
        String name,
        String hostPrefix,
        String korName
) {
    public static UserCategoryNameResult from(CategoryName name) {
        return new UserCategoryNameResult(name.getName(), name.getShortName(), name.getKorName());
    }
}
