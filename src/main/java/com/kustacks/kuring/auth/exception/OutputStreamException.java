package com.kustacks.kuring.auth.exception;

public class OutputStreamException extends AuthException {

    public static final String MESSAGE = "OutputStream을 만들 수 없습니다";

    public OutputStreamException() {
        super(MESSAGE);
    }
}
