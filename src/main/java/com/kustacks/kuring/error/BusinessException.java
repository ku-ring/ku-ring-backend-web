package com.kustacks.kuring.error;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
}
