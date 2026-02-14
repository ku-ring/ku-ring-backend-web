package com.kustacks.kuring.common.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    /**
     * ErrorCode about APIException
     */
    API_ADMIN_MISSING_PARAM(HttpStatus.BAD_REQUEST, "관리자 요청 - 필수 파라미터가 없습니다."),
    API_ADMIN_UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "관리자 요청 - 유효한 인증 토큰이 아닙니다."),
    API_ADMIN_INVALID_FCM(HttpStatus.BAD_REQUEST, "관리자 요청 - 유효한 FCM 토큰이 아닙니다."),
    API_ADMIN_INVALID_SUBJECT(HttpStatus.BAD_REQUEST, "관리자 요청 - 제목 길이가 유효하지 않습니다."),
    API_ADMIN_INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "관리자 요청 - 지원하지 않는 카테고리입니다."),
    API_ADMIN_INVALID_POSTED_DATE(HttpStatus.BAD_REQUEST, "관리자 요청 - 잘못된 형식의 게시일입니다. yyyyMMdd 형식만 지원합니다."),
    API_ADMIN_INVALID_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 type입니다."),
    API_ADMIN_INVALID_TITLE(HttpStatus.BAD_REQUEST, "title은 최소 1자 이상이어야 합니다."),
    API_ADMIN_INVALID_BODY(HttpStatus.BAD_REQUEST, "body는 최소 1자 이상이어야 합니다."),


    API_NOTICE_NOT_EXIST_CATEGORY(HttpStatus.BAD_REQUEST, "해당 공지 카테고리를 지원하지 않습니다."),
    API_MISSING_PARAM(HttpStatus.BAD_REQUEST, "필수 파라미터가 없습니다."),
    API_INVALID_PARAM(HttpStatus.BAD_REQUEST, "파라미터 값 중 잘못된 값이 있습니다."),
    API_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    API_NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "요청한 미디어 타입으로 응답할 수 없습니다."),

    API_FB_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "카테고리 구독에 실패했습니다."),
    API_FB_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    API_FB_CANNOT_EDIT_CATEGORY(HttpStatus.INTERNAL_SERVER_ERROR, "FCM에서 카테고리 편집을 실패했습니다."),

    API_FD_INVALID_CONTENT(HttpStatus.BAD_REQUEST, "피드백 길이가 유효하지 않습니다."),

    API_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류"),

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
    KU_LOGIN_IMPOSSIBLE("로그인 요청 방식이 변경된 듯 합니다. 관리자의 확인이 필요합니다."),

    KU_NOTICE_CANNOT_PARSE_JSON("kuis 공지를 POJO로 변환할 수 없습니다."),

    LIB_BAD_RESPONSE("도서관 공지 요청에 대한 응답이 비정상적입니다."),
    LIB_CANNOT_PARSE_JSON("도서관 공지를 POJO로 변환할 수 없습니다."),

    CAT_NOT_EXIST_CATEGORY(HttpStatus.BAD_REQUEST, "서버에서 지원하지 않는 카테고리입니다."),


    CLUB_CATEGORY_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "서버에서 지원하지 않는 동아리 카테고리입니다."),
    CLUB_DIVISION_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "서버에서 지원하지 않는 동아리 소속입니다."),


    STAFF_SCRAPER_EXCEED_RETRY_LIMIT("교직원 업데이트 재시도 횟수를 초과했습니다."),
    STAFF_SCRAPER_CANNOT_SCRAP("건국대학교 홈페이지가 불안정합니다. 교직원 정보를 가져올 수 없습니다."),
    STAFF_SCRAPER_CANNOT_PARSE("교직원 페이지 HTML 파싱에 실패했습니다."),

    NOTICE_SCRAPER_CANNOT_SCRAP("학과 홈페이지가 불안정합니다. 공지 정보를 가져올 수 없습니다."),
    NOTICE_SCRAPER_CANNOT_PARSE("공지 페이지 HTML 파싱에 실패했습니다."),
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 공지를 찾을 수 없습니다."),


    FB_FAIL_SUBSCRIBE(HttpStatus.INTERNAL_SERVER_ERROR, "카테고리 구독에 실패했습니다."),
    FB_FAIL_UNSUBSCRIBE(HttpStatus.INTERNAL_SERVER_ERROR, "카테고리 구독 해제에 실패했습니다."),
    FB_FAIL_ROLLBACK(HttpStatus.INTERNAL_SERVER_ERROR, "카테고리 편집 중 transaction fail이 발생했고, 이를 복구하는데 실패했습니다."),
    FB_FAIL_SEND(HttpStatus.INTERNAL_SERVER_ERROR, "FCM 메세지 전송에 실패했습니다."),

    WS_CANNOT_PARSE_JSON("JSON 문자열을 객체로 변환하는데 실패했습니다."),
    WS_CANNOT_STRINGIFY("객체를 JSON 문자열로 변경하는데 실패했습니다."),
    WS_CANNOT_SEND("웹소켓이 메세지 전송에 실패했습니다."),

    AD_UNAUTHENTICATED("관리자가 아닙니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."),
    COMMENT_BAD_WORD_CONTAINS(HttpStatus.UNPROCESSABLE_ENTITY, "금지어가 포함되어 있습니다."),

    ROOT_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "계정을 찾을 수 없습니다."),
    ROOT_USER_MISMATCH_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸습니다."),
    USER_MISMATCH_DEVICE(HttpStatus.BAD_REQUEST, "로그인한 사용자가 아닙니다."),
    USER_ALREADY_LOGIN(HttpStatus.BAD_REQUEST, "이미 로그인된 사용자입니다."),

    JWT_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 Access Token입니다."),

    EMAIL_NO_SUCH_ALGORITHM(HttpStatus.INTERNAL_SERVER_ERROR, "랜덤 숫자 생성 간 알고리즘을 찾을 수 없습니다."),
    EMAIL_INVALID_SUFFIX(HttpStatus.BAD_REQUEST, "건국대학교 이메일 도메인이 아닙니다."),
    EMAIL_INVALID_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 인증코드입니다."),
    EMAIL_INVALID_TEMPLATE(HttpStatus.BAD_REQUEST, "잘못된 이메일 발송 양식입니다."),
    EMAIL_DUPLICATE(HttpStatus.BAD_REQUEST, "중복된 사용자 이메일입니다."),

    REPORT_COMMENT_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 신고된 댓글입니다."),
    REPORT_INVALID_TARGET_TYPE(HttpStatus.NOT_FOUND, "잘못된 신고 타입입니다."),
    // AI 관련
    AI_SIMILAR_DOCUMENTS_NOT_FOUND(HttpStatus.NOT_FOUND, "죄송합니다, 해당 내용은 2024년도 6월 이후에 작성된 공지 내용에서 확인할 수 없는 내용입니다."),

    ACADEMIC_EVENT_INVALID_RANGE(HttpStatus.BAD_REQUEST, "시작일, 종료일 설정이 잘못되었습니다."),

    /**
     * ErrorCodes about DomainLogicException
     */
    DOMAIN_CANNOT_CREATE("해당 도메인을 생성할 수 없습니다."),
    DEPARTMENT_NOT_FOUND("해당 학과를 찾을 수 없습니다."),
    QUESTION_COUNT_NOT_ENOUGH(HttpStatus.TOO_MANY_REQUESTS, "남은 질문 횟수가 부족합니다."),

    STORAGE_S3_SDK_PROBLEM(HttpStatus.INTERNAL_SERVER_ERROR, "S3 클라이언트 통신 간 에러가 발생했습니다."),
    FILE_IO_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "파일을 읽어들이는데 문제가 발생했습니다.")
    ;

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
