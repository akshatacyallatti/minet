package com.example.minet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GenericResponse {
    private String message;
    private int statusCode;
    private String status;
    private LocalDateTime timestamp;
}
