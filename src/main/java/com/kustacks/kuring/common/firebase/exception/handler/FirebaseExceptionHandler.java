package com.kustacks.kuring.common.firebase.exception.handler;

import com.kustacks.kuring.common.exception.ErrorResponse;
import com.kustacks.kuring.common.firebase.exception.FirebaseInvalidTokenException;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class FirebaseExceptionHandler {

    @ExceptionHandler
    public ErrorResponse FirebaseInvalidTokenException(FirebaseInvalidTokenException exception) {
        log.error("[FirebaseInvalidTokenException] {}", exception.getMessage());
        Sentry.captureException(exception);
        return new ErrorResponse(exception.getErrorCode());
    }
}
