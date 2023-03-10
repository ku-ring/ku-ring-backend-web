package com.kustacks.kuring.common.dto;

import lombok.Getter;

@Getter
public class BaseResponse<T> {

    private final int code; // HttpStatus code

    private final String message;

    private final T data;

    public BaseResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public BaseResponse(ResponseCodeAndMessages response, T data) {
        this(response.getCode(), response.getMessage(), data);
    }
}
