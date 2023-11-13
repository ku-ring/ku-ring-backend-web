package com.kustacks.kuring.auth.exception;

import com.kustacks.kuring.common.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.AUTH_AUTHENTICATION_FAIL;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<BaseResponse> RoleAuthenticationExceptionHandler(RoleAuthenticationException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new BaseResponse<>(AUTH_AUTHENTICATION_FAIL, null));
    }
}
