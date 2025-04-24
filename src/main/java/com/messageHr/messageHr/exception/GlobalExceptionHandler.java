package com.messageHr.messageHr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.messageHr.messageHr.response.ApiResponse;

@RestControllerAdvice

public class GlobalExceptionHandler {

    @ExceptionHandler
    (IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(new ApiResponse("Invalid argument: " + ex.getMessage(), null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(new ApiResponse("Runtime error: " + ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneralException(Exception ex) {
        return new ResponseEntity<>(new ApiResponse("Unexpected error: " + ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
