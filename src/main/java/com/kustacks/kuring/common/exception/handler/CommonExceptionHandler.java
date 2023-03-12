package com.kustacks.kuring.common.exception.handler;

import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.ErrorResponse;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler
    public ErrorResponse MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception) {
        log.error("[MethodArgumentNotValidException] {}", exception.getMessage());
        Sentry.captureException(exception);
        return new ErrorResponse(ErrorCode.API_MISSING_PARAM);
    }
}
