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
    API_NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "요청한 미디어 타입으로 응답할 수 없습니다."),

    API_FB_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "카테고리 구독에 실패했습니다."),
    API_FB_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    API_FB_CANNOT_EDIT_CATEGORY(HttpStatus.INTERNAL_SERVER_ERROR, "FCM에서 카테고리 편집을 실패했습니다."),

    API_FD_INVALID_CONTENT(HttpStatus.BAD_REQUEST, "피드백 길이가 유효하지 않습니다."),

    API_AD_UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "관리자가 아닙니다."),

    API_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류"),

    /**
     * ErrorCodes about WebSocket
     */
    WS_MISSING_PARAM(HttpStatus.BAD_REQUEST, "웹소켓 메세지의 파라미터가 누락되어있습니다."),
    WS_INVALID_PARAM(HttpStatus.BAD_REQUEST, "웹소켓 메세지 파라미터 중 유효하지 않은 값이 있습니다."),
    WS_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."),

    /**
     * ErrorCodes about InternalLogicException
     */
    KU_LOGIN_CANNOT_LOGIN("kuis 로그인 요청이 실패했습니다."),
    KU_LOGIN_NO_RESPONSE_BODY("kuis 로그인 요청에 대한 응답 body를 찾을 수 없습니다."),
    KU_LOGIN_BAD_RESPONSE("kuis 로그인 요청에 대한 응답이 비정상적입니다."),
    KU_LOGIN_NO_COOKIE_HEADER("kuis 로그인 요청에 대한 응답에 Set-Cookie 헤더가 없습니다."),
    KU_LOGIN_EMPTY_COOKIE("kuis 로그인 요청에 대한 응답 중 Set-Cookie 헤더가 비어 있습니다."),
    KU_LOGIN_NO_JSESSION("kuis 로그인 요청에 대한 응답에 Set-Cookie 헤더값이 있지만, JSESSIONID가 없습니다."),
    KU_LOGIN_CANNOT_GET_API_SKELETON("ku-boost에서 제공하는 api skeleton을 가져올 수 없습니다."),
    KU_LOGIN_CANNOT_PARSE_API_SKELETON("ku-boost에서 제공하는 api skeleton을 파싱에서 오류가 발생했습니다."),

    KU_NOTICE_CANNOT_PARSE_JSON("kuis 공지를 POJO로 변환할 수 없습니다."),

    LIB_BAD_RESPONSE("도서관 공지 요청에 대한 응답이 비정상적입니다."),
    LIB_CANNOT_PARSE_JSON("도서관 공지를 POJO로 변환할 수 없습니다."),

    CAT_NOT_EXIST_CATEGORY("서버에서 지원하지 않는 카테고리입니다."),

//    STAFF_SCRAPER_TAG_NOT_EXIST("Jsoup - 찾고자 하는 태그가 존재하지 않습니다."),
    STAFF_SCRAPER_EXCEED_RETRY_LIMIT("교직원 업데이트 재시도 횟수를 초과했습니다."),
    STAFF_SCRAPER_CANNOT_SCRAP("건국대학교 홈페이지가 불안정합니다. 교직원 정보를 가져올 수 없습니다."),
    STAFF_SCRAPER_CANNOT_PARSE("교직원 페이지 HTML 파싱에 실패했습니다."),

    FB_FAIL_SUBSCRIBE("카테고리 구독에 실패했습니다."),
    FB_FAIL_UNSUBSCRIBE("카테고리 구독 해제에 실패했습니다."),
    FB_FAIL_ROLLBACK("카테고리 편집 중 transaction fail이 발생했고, 이를 복구하는데 실패했습니다."),
    FB_FAIL_SEND("FCM 메세지 전송에 실패했습니다."),

    WS_CANNOT_PARSE_JSON("JSON 문자열을 객체로 변환하는데 실패했습니다."),
    WS_CANNOT_STRINGIFY("객체를 JSON 문자열로 변경하는데 실패했습니다."),
    WS_CANNOT_SEND("웹소켓이 메세지 전송에 실패했습니다."),

    AD_UNAUTHENTICATED("관리자가 아닙니다.");

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
