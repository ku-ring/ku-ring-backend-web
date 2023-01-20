package com.kustacks.kuring.common.dto;

import lombok.Getter;

@Getter
public class SubscribeCategoriesResponseDto extends ResponseDto {

    public SubscribeCategoriesResponseDto() {
        super(true, "성공", 201);
    }
}
