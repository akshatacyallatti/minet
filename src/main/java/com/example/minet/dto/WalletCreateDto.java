package com.example.minet.dto;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class WalletCreateDto {
    private Integer userId;
    private Integer currencyId;
    private BigDecimal balance;
    private  String currencyCode;
}


