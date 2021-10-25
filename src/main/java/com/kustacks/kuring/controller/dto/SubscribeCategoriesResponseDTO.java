package com.kustacks.kuring.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SubscribeCategoriesResponseDTO extends ResponseDTO {

    @Builder
    public SubscribeCategoriesResponseDTO() {
        super(true, "성공", 201);
    }
}
