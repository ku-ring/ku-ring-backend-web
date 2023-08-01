package com.kustacks.kuring.user.exception.handler;

import com.kustacks.kuring.common.exception.ErrorCode;
import com.kustacks.kuring.common.exception.ErrorResponse;
import com.kustacks.kuring.user.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler
    public ErrorResponse invalidId(UserNotFoundException exception) {
        log.info("[UserNotFoundException] {}", exception.getMessage());
        return new ErrorResponse(ErrorCode.USER_NOT_FOUND);
    }
}
