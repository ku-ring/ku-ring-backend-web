package com.kustacks.kuring.controller.dto;

import org.springframework.http.HttpStatus;

public class UserEnrollNewlyCreatedResponseDTO extends UserEnrollResponseDTO {
    public UserEnrollNewlyCreatedResponseDTO() {
        super(true, "성공", HttpStatus.CREATED.value());
    }
}
