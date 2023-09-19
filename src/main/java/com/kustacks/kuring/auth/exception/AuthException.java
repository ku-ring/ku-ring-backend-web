package com.kustacks.kuring.auth.exception;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {

    public AuthException(String message) {
        super(message);
    }
}
