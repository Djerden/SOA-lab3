package com.djeno.genocide_service.exceptions;

import com.djeno.genocide_service.persistence.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCityNotFoundException(CityNotFoundException e) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage())
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientErrorException(HttpClientErrorException e) {
        ErrorResponse error = ErrorResponse.builder()
                .status(e.getStatusCode().value())
                .message("Error communicating with city-service: " + e.getMessage())
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(e.getStatusCode()).body(error);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleResourceAccessException(ResourceAccessException e) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message("City service is unavailable: " + e.getMessage())
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Internal server error: " + e.getMessage())
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
