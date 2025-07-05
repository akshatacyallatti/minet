package com.example.minet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequestDto {
    private Integer userId;
    private Integer currencyId;
    private  String currencyCode;
    @NotNull
    private Double amount;
    @NotNull
    private Double price;
    @NotBlank
    private String orderType;
}

