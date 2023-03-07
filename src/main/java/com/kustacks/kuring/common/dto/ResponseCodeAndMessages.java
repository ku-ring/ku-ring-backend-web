package com.kustacks.kuring.common.dto;

import org.springframework.http.HttpStatus;

public enum ResponseCodeAndMessages {

    /* Notice */
    NOTICE_SEARCH_SUCCESS(HttpStatus.OK.value(), "공지 조회에 성공하였습니다");

    private final int code;
    private final String message;

    ResponseCodeAndMessages(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
