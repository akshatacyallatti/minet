package com.example.minet.dto;

import com.example.minet.constants.OrderStatus;
import com.example.minet.constants.OrderType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponseDto {

    private Integer orderId;
    private OrderType orderType;
    private BigDecimal balance;
    private BigDecimal totalValue;
    private OrderStatus status;
    private String currencySymbol;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
