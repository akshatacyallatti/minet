package com.example.minet.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CurrencyRequestDto {

    private Integer currencyId;
    @NonNull
    private String currencyName;
    @NonNull
    private String currencyCode;
    @NonNull
    private String symbol;
    @NonNull
    private Double changePercent;
    @NonNull
    private BigDecimal price;
    @NonNull
    private BigDecimal marketCap;
}
