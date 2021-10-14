package com.kustacks.kuring.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseDTO {
    private boolean isSuccess;
    private String resultMsg;
    private int resultCode;
}
