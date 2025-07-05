package com.example.minet.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponseDto {
    private Integer userId;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}
