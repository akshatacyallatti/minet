package com.example.minet.utils;

import com.example.minet.dto.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class ResponseBuilder {

    public static ResponseEntity<GenericResponse> success(String message) {
        return build(message, HttpStatus.OK);
    }

    public static ResponseEntity<GenericResponse> created(String message) {
        return build(message, HttpStatus.CREATED);
    }

    public static ResponseEntity<GenericResponse> badRequest(String message) {
        return build(message, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<GenericResponse> conflict(String message) {
        return build(message, HttpStatus.CONFLICT);
    }

    public static ResponseEntity<GenericResponse> unauthorized(String message) {
        return build(message, HttpStatus.UNAUTHORIZED);
    }

    public static ResponseEntity<GenericResponse> build(String message, HttpStatus status) {
        return new ResponseEntity<>(
                new GenericResponse(message, status.value(), status.getReasonPhrase(), LocalDateTime.now()),
                status
        );
    }
}
