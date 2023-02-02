package com.kustacks.kuring.category.exception.handler;

import com.kustacks.kuring.category.exception.CategoryNotFoundException;
import com.kustacks.kuring.common.error.ErrorResponse;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CategoryExceptionHandler {

    @ExceptionHandler
    public ErrorResponse NotFoundCategoryExceptionHandler(CategoryNotFoundException exception) {
        log.error("[NotFoundCategoryException] {}", exception.getMessage());
        Sentry.captureException(exception);
        return new ErrorResponse(exception.getErrorCode());
    }
}
