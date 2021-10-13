package com.kustacks.kuring.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {

    /**
     * ErrorCode about APIException
     */


    /**
     * ErrorCodes about InternalLogicException
     */
    KU_LOGIN_NO_RESPONSE_BODY("kuis 로그인 요청에 대한 응답 body를 찾을 수 없습니다."),
    KU_LOGIN_BAD_RESPONSE("kuis 로그인 요청에 대한 응답이 비정상적입니다."),
    KU_LOGIN_NO_COOKIE_HEADER("kuis 로그인 요청에 대한 응답에 Set-Cookie 헤더가 없습니다."),
    KU_LOGIN_EMPTY_COOKIE("kuis 로그인 요청에 대한 응답 중 Set-Cookie 헤더가 비어 있습니다."),
    KU_LOGIN_NO_JSESSION("kuis 로그인 요청에 대한 응답에 Set-Cookie 헤더값이 있지만, JSESSIONID가 없습니다."),

    KU_NOTICE_CANNOT_PARSE_JSON("kuis 공지를 POJO로 변환할 수 없습니다.");


    private HttpStatus httpStatus;
    private String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    ErrorCode(String message) {
        this.httpStatus = null;
        this.message = message;
    }
}
