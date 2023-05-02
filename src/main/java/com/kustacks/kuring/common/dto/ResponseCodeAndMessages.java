package com.kustacks.kuring.common.dto;

import org.springframework.http.HttpStatus;

public enum ResponseCodeAndMessages {

    /* Notice */
    NOTICE_SEARCH_SUCCESS(HttpStatus.OK.value(), "공지 조회에 성공하였습니다"),

    /* Category */
    CATEGORY_SEARCH_SUCCESS(HttpStatus.OK.value(), "지원하는 학교 공지 카테고리 조회에 성공하였습니다"),
    CATEGORY_USER_SUBSCRIBES_LOOKUP_SUCCESS(HttpStatus.OK.value(), "사용자가 구독한 학교 공지 카테고리 조회에 성공하였습니다"),

    /* Department */
    DEPARTMENTS_SEARCH_SUCCESS(HttpStatus.OK.value(), "지원하는 학과 조회에 성공하였습니다"),
    DEPARTMENTS_SUBSCRIBE_SUCCESS(HttpStatus.OK.value(), "사용자의 학과 구독에 성공하였습니다"),
    DEPARTMENTS_USER_SUBSCRIBES_LOOKUP_SUCCESS(HttpStatus.OK.value(), "사용자가 구독한 학과 조회에 성공하였습니다"),

    /* Staff */
    STAFF_SEARCH_SUCCESS(HttpStatus.OK.value(), "교직원 조회에 성공하였습니다");

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
