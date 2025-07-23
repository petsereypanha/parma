package com.parma.user.exception;

import com.parma.common.exception.GeneralErrorResponse;
import com.parma.common.exception.ResponseErrorTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalException {

//    @ExceptionHandler(UserValidationException.class)
//    public ResponseEntity<ErrorResponse> handleUserValidationException(UserValidationException e) {
//
//        ErrorResponse errorResponse = new ErrorResponse("400", e.getMessage(), e.getField(), e.getValue());
//
//
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(RoleValidationException.class)
//    public ResponseEntity<ErrorResponse> handleRoleValidationException(RoleValidationException e) {
//        ErrorResponse errorResponse = new ErrorResponse("400", e.getMessage(), e.getField(), e.getValue());
//
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseErrorTemplate> handle(Exception e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(
                GeneralErrorResponse.generalError(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}