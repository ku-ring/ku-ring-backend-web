package com.kustacks.kuring.error;

import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ErrorController {
    @ExceptionHandler({MissingServletRequestParameterException.class, ConstraintViolationException.class})
    public ErrorResponse handleBadRequestException(Exception e) {
        if(e instanceof MissingServletRequestParameterException) {
            return new ErrorResponse(ErrorCode.API_MISSING_PARAM);
        } else if(e instanceof ConstraintViolationException) {
            return new ErrorResponse(ErrorCode.API_INVALID_PARAM);
        }
        return new ErrorResponse(ErrorCode.API_BAD_REQUEST);
    }

    @ExceptionHandler(InternalLogicException.class)
    public ErrorResponse handleInternalLogicException(InternalLogicException e) {
        return new ErrorResponse(e.getErrorCode());
    }

    @ExceptionHandler(APIException.class)
    public ErrorResponse handleAPIException(APIException e) {
        return new ErrorResponse(e.getErrorCode());
    }
}
