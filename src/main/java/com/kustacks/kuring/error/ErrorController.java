package com.kustacks.kuring.error;

import io.sentry.Sentry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolationException;
import java.sql.SQLException;

@ControllerAdvice
public class ErrorController {

    private final Logger log = LoggerFactory.getLogger(ErrorController.class);

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            ConstraintViolationException.class,
            HttpMediaTypeNotAcceptableException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMessageNotReadableException.class // API 요청 시 필요한 body가 없을 경우 발생하는 오류 
    })
    public @ResponseBody ErrorResponse handleBadRequestException(Exception e) {

        log.error("[BadAccessException] {}", e.getMessage(), e);
        Sentry.captureException(e);

        if(e instanceof MissingServletRequestParameterException) {
            return new ErrorResponse(ErrorCode.API_MISSING_PARAM);
        } else if(e instanceof HttpMessageNotReadableException) {
            return new ErrorResponse(ErrorCode.API_MISSING_BODY);
        } else if(e instanceof HttpRequestMethodNotSupportedException) {
            return new ErrorResponse(ErrorCode.API_INVALID_HEADER);
        } else if(e instanceof ConstraintViolationException) {
            return new ErrorResponse(ErrorCode.API_INVALID_PARAM);
        } else if(e instanceof HttpMediaTypeNotAcceptableException) {
            return new ErrorResponse(ErrorCode.API_NOT_ACCEPTABLE);
        }

        return new ErrorResponse(ErrorCode.API_BAD_REQUEST);
    }

    @ExceptionHandler(SQLException.class)
    public @ResponseBody ErrorResponse handleSQLException(SQLException e) {
        log.error("[SQLException] {}", e.getMessage());
        Sentry.captureException(e);
        return new ErrorResponse(ErrorCode.API_SERVER_ERROR);
    }

    @ExceptionHandler(InternalLogicException.class)
    public void handleInternalLogicException(InternalLogicException e) {
        log.error("[InternalLogicException] {}", e.getErrorCode().getMessage(), e);
//        Sentry.captureException(e);
    }

    @ExceptionHandler(APIException.class)
    public @ResponseBody ErrorResponse handleAPIException(APIException e) {

        log.error("[APIException] {}", e.getErrorCode().getMessage(), e);

        ErrorCode errorCode = e.getErrorCode();
        if(errorCode.equals(ErrorCode.API_ADMIN_UNAUTHENTICATED) ||
            errorCode.equals(ErrorCode.API_ADMIN_INVALID_CATEGORY) ||
                errorCode.equals(ErrorCode.API_ADMIN_INVALID_FCM) ||
                errorCode.equals(ErrorCode.API_ADMIN_INVALID_POSTED_DATE) ||
                errorCode.equals(ErrorCode.API_ADMIN_INVALID_SUBJECT) ||
                errorCode.equals(ErrorCode.API_ADMIN_MISSING_PARAM)) {

        } else {
            Sentry.captureException(e);
        }

        return new ErrorResponse(e.getErrorCode());
    }
}
