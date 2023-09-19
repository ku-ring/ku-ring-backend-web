package com.kustacks.kuring.auth.exception;

public class UnauthorizedException extends RuntimeException {

    public static final String MESSAGE = "권한이 없는 사용자 입니다";

    public UnauthorizedException() {
        super(MESSAGE);
    }
}
