package com.kustacks.kuring.common.exception.handler;

import com.kustacks.kuring.common.dto.ErrorResponse;
import com.kustacks.kuring.common.exception.AdminException;
import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.message.application.service.exception.FirebaseSubscribeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception) {
        log.error("[MethodArgumentNotValidException] {}", exception.getMessage());
        return ResponseEntity.status(ErrorCode.API_MISSING_PARAM.getHttpStatus())
                .body(new ErrorResponse(ErrorCode.API_MISSING_PARAM));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> NotFoundExceptionHandler(NotFoundException exception) {
        log.error("[NotFoundException] {}", exception.getMessage());
        return ResponseEntity.status(exception.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(exception.getErrorCode()));
    }

    @ExceptionHandler(AdminException.class)
    public ResponseEntity<ErrorResponse> AdminExceptionHandler(AdminException exception) {
        log.error("[APIException] {}", exception.getErrorCode().getMessage(), exception);
        return ResponseEntity.status(exception.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(exception.getErrorCode()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> MissingServletRequestParameterExceptionHandler(MissingServletRequestParameterException exception) {
        log.error("[MissingServletRequestParameterException] {}", exception.getMessage());
        return ResponseEntity.status(ErrorCode.API_MISSING_PARAM.getHttpStatus())
                .body(new ErrorResponse(ErrorCode.API_MISSING_PARAM));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> ConstraintViolationExceptionHandler(ConstraintViolationException exception) {
        log.error("[ConstraintViolationException] {}", exception.getMessage());
        return ResponseEntity.status(ErrorCode.API_INVALID_PARAM.getHttpStatus())
                .body(new ErrorResponse(ErrorCode.API_INVALID_PARAM));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> HttpMediaTypeNotAcceptableExceptionHandler(HttpMediaTypeNotAcceptableException exception) {
        log.error("[HttpMediaTypeNotAcceptableException] {}", exception.getMessage());
        return ResponseEntity.status(ErrorCode.API_NOT_ACCEPTABLE.getHttpStatus())
                .body(new ErrorResponse(ErrorCode.API_NOT_ACCEPTABLE));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> FirebaseSubscribeExceptionHandler(FirebaseSubscribeException exception) {
        log.error("[FirebaseSubscribeException] {}", exception.getMessage());
        return ResponseEntity.status(ErrorCode.API_FB_SERVER_ERROR.getHttpStatus())
                .body(new ErrorResponse(ErrorCode.API_FB_SERVER_ERROR));
    }

    @ExceptionHandler
    public void InternalLogicExceptionHandler(InternalLogicException e) {
        log.error("[InternalLogicException] {}", e.getErrorCode().getMessage(), e);
    }
}
