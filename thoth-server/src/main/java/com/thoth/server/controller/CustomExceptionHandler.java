package com.thoth.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        ex.printStackTrace();
        if(ex instanceof AccessDeniedException){
            ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED, ex.getLocalizedMessage());
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }else {
            ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private record ErrorResponse(HttpStatus status, String message) {
    }
}