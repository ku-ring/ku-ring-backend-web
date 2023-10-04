package com.kustacks.kuring.common.dto;

import org.springframework.http.HttpStatus;

public enum ResponseCodeAndMessages {

    /* Notice */
    NOTICE_SEARCH_SUCCESS(HttpStatus.OK.value(), "공지 조회에 성공하였습니다"),

    /* Category */
    CATEGORY_SEARCH_SUCCESS(HttpStatus.OK.value(), "지원하는 학교 공지 카테고리 조회에 성공하였습니다"),
    CATEGORY_SUBSCRIBE_SUCCESS(HttpStatus.OK.value(), "사용자의 학교 공지 카테고리 구독에 성공하였습니다"),

    CATEGORY_USER_SUBSCRIBES_LOOKUP_SUCCESS(HttpStatus.OK.value(), "사용자가 구독한 학교 공지 카테고리 조회에 성공하였습니다"),

    /* Department */
    DEPARTMENTS_SEARCH_SUCCESS(HttpStatus.OK.value(), "지원하는 학과 조회에 성공하였습니다"),
    DEPARTMENTS_SUBSCRIBE_SUCCESS(HttpStatus.OK.value(), "사용자의 학과 구독에 성공하였습니다"),
    DEPARTMENTS_USER_SUBSCRIBES_LOOKUP_SUCCESS(HttpStatus.OK.value(), "사용자가 구독한 학과 조회에 성공하였습니다"),

    /* Staff */
    STAFF_SEARCH_SUCCESS(HttpStatus.OK.value(), "교직원 조회에 성공하였습니다"),

    /* Feedback */
    FEEDBACK_SAVE_SUCCESS(HttpStatus.OK.value(), "피드백 저장에 성공하였습니다"),
    FEEDBACK_SEARCH_SUCCESS(HttpStatus.OK.value(), "피드백 조회에 성공하였습니다"),

    /* Admin */
    ADMIN_TEST_NOTICE_CREATE_SUCCESS(HttpStatus.OK.value(), "테스트 공지 생성에 성공하였습니다"),
    ADMIN_REAL_NOTICE_CREATE_SUCCESS(HttpStatus.OK.value(), "실제 공지 생성에 성공하였습니다"),

    /* User */
    USER_REGISTER_SUCCESS(HttpStatus.OK.value(), "회원가입에 성공하였습니다"),
    USER_REGISTER_FAIL(HttpStatus.BAD_REQUEST.value(), "회원가입에 실패하였습니다"),

    /**
     * ErrorCodes about auth
     */
    AUTH_AUTHENTICATION_SUCCESS(HttpStatus.OK.value(), "인증에 성공하였습니다"),
    AUTH_AUTHENTICATION_FAIL(HttpStatus.UNAUTHORIZED.value(), "인증에 실패하였습니다");

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
