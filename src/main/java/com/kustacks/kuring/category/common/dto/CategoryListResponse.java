package com.kustacks.kuring.category.common.dto;

import com.kustacks.kuring.common.dto.ResponseDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryListResponse extends ResponseDto {

    private List<String> categories;

    public CategoryListResponse(List<String> categories) {
        super(true, "성공", 200);
        this.categories = categories;
    }
}
