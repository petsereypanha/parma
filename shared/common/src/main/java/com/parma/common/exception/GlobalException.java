package com.parma.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseErrorTemplate> handle(Exception e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(
                GeneralErrorResponse.generalError(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CustomMessageException.class)
    public ResponseEntity<ResponseErrorTemplate> handle(CustomMessageException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(
                new ResponseErrorTemplate(
                        e.getMessage(),
                        e.getCode(),
                        e.getObject(),
                        true),
                e.getHttpStatus());
    }
}
