package com.kustacks.kuring.controller.dto;

public class UserEnrollResponseDTO extends ResponseDTO {
    public UserEnrollResponseDTO(boolean isSuccess, String resultMsg, int resultCode) {
        super(isSuccess, resultMsg, resultCode);
    }
}
