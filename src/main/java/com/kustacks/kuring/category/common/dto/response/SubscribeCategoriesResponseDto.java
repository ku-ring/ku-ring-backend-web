package com.kustacks.kuring.category.common.dto.response;

import com.kustacks.kuring.common.dto.ResponseDto;
import lombok.Getter;

@Getter
public class SubscribeCategoriesResponseDto extends ResponseDto {

    public SubscribeCategoriesResponseDto() {
        super(true, "성공", 201);
    }
}
