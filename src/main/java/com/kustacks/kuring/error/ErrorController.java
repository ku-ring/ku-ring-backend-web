package com.kustacks.kuring.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ErrorController {

    private final Logger log = LoggerFactory.getLogger(ErrorController.class);

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            ConstraintViolationException.class,
            HttpMediaTypeNotAcceptableException.class
    })
    public ErrorResponse handleBadRequestException(Exception e) {
        if(e instanceof MissingServletRequestParameterException) {
            return new ErrorResponse(ErrorCode.API_MISSING_PARAM);
        } else if(e instanceof ConstraintViolationException) {
            return new ErrorResponse(ErrorCode.API_INVALID_PARAM);
        } else if(e instanceof HttpMediaTypeNotAcceptableException) {
            return new ErrorResponse(ErrorCode.API_NOT_ACCEPTABLE);
        }
        return new ErrorResponse(ErrorCode.API_BAD_REQUEST);
    }

    @ExceptionHandler(InternalLogicException.class)
    public void handleInternalLogicException(InternalLogicException e) {
        log.error("[InternalLogicException] {}", e.getErrorCode().getMessage());
    }

    @ExceptionHandler(APIException.class)
    public ErrorResponse handleAPIException(APIException e) {
        log.error("[APIException] {}", e.getErrorCode().getMessage());
        return new ErrorResponse(e.getErrorCode());
    }
}
