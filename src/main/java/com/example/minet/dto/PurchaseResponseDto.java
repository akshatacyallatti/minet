package com.example.minet.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PurchaseResponseDto {
    private String status;              // Add this
    private String message;
    private String currencySymbol;      // e.g. BTC
    private BigDecimal cryptoAmount;    // cryptoPurchased
    private BigDecimal usdSpent;        // totalUsdCharged
    private BigDecimal fee;             // Add this
    private LocalDateTime purchasedAt;  // Add this if you're showing timestamp
}
