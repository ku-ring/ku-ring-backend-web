package com.kustacks.kuring.controller.dto;

import org.springframework.stereotype.Component;

@Component
public class SubscribeCategoriesResponseDTO extends ResponseDTO {
    public SubscribeCategoriesResponseDTO() {
        super(true, "성공", 201);
    }
}
