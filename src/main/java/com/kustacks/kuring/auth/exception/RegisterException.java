package com.kustacks.kuring.auth.exception;

public class RegisterException extends AuthException {

    public static final String MESSAGE = "사용자를 등록할 수 없습니다.";

    public RegisterException() {
        super(MESSAGE);
    }
}
