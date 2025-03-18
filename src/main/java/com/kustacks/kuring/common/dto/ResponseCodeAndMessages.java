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

    /* Admin */
    ADMIN_TEST_NOTICE_CREATE_SUCCESS(HttpStatus.OK.value(), "테스트 공지 생성에 성공하였습니다"),
    ADMIN_REAL_NOTICE_CREATE_SUCCESS(HttpStatus.OK.value(), "실제 공지 생성에 성공하였습니다"),
    ADMIN_EMBEDDING_NOTICE_SUCCESS(HttpStatus.OK.value(), "데이터 임베딩에 생성에 성공하였습니다"),

    /* User */
    USER_REGISTER_SUCCESS(HttpStatus.OK.value(), "회원가입에 성공하였습니다"),
    USER_REGISTER_FAIL(HttpStatus.BAD_REQUEST.value(), "회원가입에 실패하였습니다"),
    BOOKMAKR_SAVE_SUCCESS(HttpStatus.OK.value(), "북마크 저장에 성공하였습니다"),
    BOOKMARK_LOOKUP_SUCCESS(HttpStatus.OK.value(), "북마크 조회에 성공하였습니다"),
    FEEDBACK_SAVE_SUCCESS(HttpStatus.OK.value(), "피드백 저장에 성공하였습니다"),
    FEEDBACK_SEARCH_SUCCESS(HttpStatus.OK.value(), "피드백 조회에 성공하였습니다"),
    ASK_COUNT_LOOKUP_SUCCESS(HttpStatus.OK.value(), "질문 가능 횟수 조회에 성공하였습니다"),
    NOTICE_COMMENT_SAVE_SUCCESS(HttpStatus.OK.value(), "공지에 댓글 추가를 성공하였습니다"),
    NOTICE_COMMENT_EDIT_SUCCESS(HttpStatus.OK.value(), "공지에 댓글 편집을 성공하였습니다"),

    USER_SIGNUP(HttpStatus.CREATED.value(), "사용자 계정 생성에 성공했습니다."),
    USER_LOGIN(HttpStatus.OK.value(), "로그인에 성공했습니다."),
    USER_LOGOUT(HttpStatus.OK.value(), "로그아웃에 성공했습니다."),
    USER_PASSWORD_MODIFY(HttpStatus.OK.value(), "비밀번호 변경에 성공했습니다."),

    /* Alert */
    ALERT_SEARCH_SUCCESS(HttpStatus.OK.value(), "예약 알림 조회에 성공하였습니다"),

    /* Email */
    EMAIL_SEND_SUCCESS(HttpStatus.OK.value(), "이메일 전송에 성공했습니다."),
    EMAIL_CODE_VERIFY_SUCCESS(HttpStatus.OK.value(),"인증에 성공했습니다."),
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
