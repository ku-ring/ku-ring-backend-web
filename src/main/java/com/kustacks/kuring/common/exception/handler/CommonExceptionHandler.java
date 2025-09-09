package com.kustacks.kuring.common.exception.handler;

import com.kustacks.kuring.common.dto.ErrorResponse;
import com.kustacks.kuring.common.exception.AdminException;
import com.kustacks.kuring.common.exception.BadWordContainsException;
import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.InvalidStateException;
import com.kustacks.kuring.common.exception.NoPermissionException;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.message.application.service.exception.FirebaseSubscribeException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> MethodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException exception) {
        log.warn("[MethodArgumentTypeMismatchException] {}", exception.getMessage());
        return ResponseEntity.status(ErrorCode.API_INVALID_PARAM.getHttpStatus())
                .body(new ErrorResponse(ErrorCode.API_INVALID_PARAM));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception) {
        log.warn("[MethodArgumentNotValidException] {}", exception.getMessage());
        return ResponseEntity.status(ErrorCode.API_MISSING_PARAM.getHttpStatus())
                .body(new ErrorResponse(ErrorCode.API_MISSING_PARAM));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> NotFoundExceptionHandler(NotFoundException exception) {
        log.warn("[NotFoundException] {}", exception.getMessage());
        return ResponseEntity.status(exception.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(exception.getErrorCode()));
    }

    @ExceptionHandler(AdminException.class)
    public ResponseEntity<ErrorResponse> AdminExceptionHandler(AdminException exception) {
        log.warn("[APIException] {}", exception.getErrorCode().getMessage(), exception);
        return ResponseEntity.status(exception.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(exception.getErrorCode()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> MissingServletRequestParameterExceptionHandler(MissingServletRequestParameterException exception) {
        log.warn("[MissingServletRequestParameterException] {}", exception.getMessage());
        return ResponseEntity.status(ErrorCode.API_MISSING_PARAM.getHttpStatus())
                .body(new ErrorResponse(ErrorCode.API_MISSING_PARAM));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> ConstraintViolationExceptionHandler(ConstraintViolationException exception) {
        log.warn("[ConstraintViolationException] {}", exception.getMessage());
        return ResponseEntity.status(ErrorCode.API_INVALID_PARAM.getHttpStatus())
                .body(new ErrorResponse(ErrorCode.API_INVALID_PARAM));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> HttpMediaTypeNotAcceptableExceptionHandler(HttpMediaTypeNotAcceptableException exception) {
        log.warn("[HttpMediaTypeNotAcceptableException] {}", exception.getMessage());
        return ResponseEntity.status(ErrorCode.API_NOT_ACCEPTABLE.getHttpStatus())
                .body(new ErrorResponse(ErrorCode.API_NOT_ACCEPTABLE));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> FirebaseSubscribeExceptionHandler(FirebaseSubscribeException exception) {
        log.warn("[FirebaseSubscribeException] {}", exception.getMessage());
        return ResponseEntity.status(ErrorCode.API_FB_SERVER_ERROR.getHttpStatus())
                .body(new ErrorResponse(ErrorCode.API_FB_SERVER_ERROR));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> InvalidStateExceptionHandler(InvalidStateException exception) {
        log.info("[InvalidStateException] {}", exception.getMessage());
        return ResponseEntity.status(exception.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(exception.getErrorCode()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> NoPermissionExceptionHandler(NoPermissionException exception) {
        log.info("[NoPermissionException] {}", exception.getMessage());
        return ResponseEntity.status(exception.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(exception.getErrorCode()));
    }

    @ExceptionHandler
    public void InternalLogicExceptionHandler(InternalLogicException e) {
        log.warn("[InternalLogicException] {}", e.getErrorCode().getMessage(), e);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> BadWordExceptionHandler(BadWordContainsException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(e.getErrorCode()));
    }
}
