package com.kustacks.kuring.controller.dto;

import org.springframework.http.HttpStatus;

public class UserEnrollAlreadyExistResponseDTO extends UserEnrollResponseDTO {
    public UserEnrollAlreadyExistResponseDTO() {
        super(true, "성공", HttpStatus.NO_CONTENT.value());
    }
}
