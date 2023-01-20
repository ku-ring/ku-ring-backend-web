package com.kustacks.kuring.common.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryListResponseDto extends ResponseDto {

    private List<String> categories;

    public CategoryListResponseDto(List<String> categories) {
        super(true, "성공", 200);
        this.categories = categories;
    }
}
