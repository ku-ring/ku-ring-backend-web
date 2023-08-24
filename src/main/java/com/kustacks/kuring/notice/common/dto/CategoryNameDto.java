package com.kustacks.kuring.notice.common.dto;

import com.kustacks.kuring.notice.domain.CategoryName;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryNameDto {

    private String name;
    private String hostPrefix;
    private String korName;

    public CategoryNameDto(String name, String hostPrefix, String korName) {
        this.name = name;
        this.hostPrefix = hostPrefix;
        this.korName = korName;
    }

    public static CategoryNameDto from(CategoryName name) {
        return new CategoryNameDto(name.getName(), name.getShortName(), name.getKorName());
    }
}
