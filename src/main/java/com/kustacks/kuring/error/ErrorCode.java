package com.kustacks.kuring.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    /**
     * ErrorCode about APIException
     */
    API_NOTICE_NOT_EXIST_CATEGORY(HttpStatus.BAD_REQUEST, "해당 공지 카테고리를 지원하지 않습니다."),
//    API_NOTICE_CANNOT_FIND_CATEGORY(HttpStatus.INTERNAL_SERVER_ERROR, "해당 공지 카테고리를 찾을 수 없습니다."),
    API_MISSING_PARAM(HttpStatus.BAD_REQUEST, "필수 파라미터가 없습니다."),
    API_INVALID_PARAM(HttpStatus.BAD_REQUEST, "파라미터 값 중 잘못된 값이 있습니다."),
    API_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    API_FB_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "카테고리 등록에 실패했습니다."),


    /**
     * ErrorCodes about InternalLogicException
     */
    KU_LOGIN_NO_RESPONSE_BODY("kuis 로그인 요청에 대한 응답 body를 찾을 수 없습니다."),
    KU_LOGIN_BAD_RESPONSE("kuis 로그인 요청에 대한 응답이 비정상적입니다."),
    KU_LOGIN_NO_COOKIE_HEADER("kuis 로그인 요청에 대한 응답에 Set-Cookie 헤더가 없습니다."),
    KU_LOGIN_EMPTY_COOKIE("kuis 로그인 요청에 대한 응답 중 Set-Cookie 헤더가 비어 있습니다."),
    KU_LOGIN_NO_JSESSION("kuis 로그인 요청에 대한 응답에 Set-Cookie 헤더값이 있지만, JSESSIONID가 없습니다."),

    KU_NOTICE_CANNOT_PARSE_JSON("kuis 공지를 POJO로 변환할 수 없습니다."),

    LIB_BAD_RESPONSE("도서관 공지 요청에 대한 응답이 비정상적입니다."),
    LIB_CANNOT_PARSE_JSON("도서관 공지를 POJO로 변환할 수 없습니다."),

    FB_FAIL_SUBSCRIBE("카테고리 구독에 실패했습니다."),
    FB_FAIL_UNSUBSCRIBE("카테고리 구독 해제에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    ErrorCode(String message) {
        this.httpStatus = null;
        this.message = message;
    }
}
