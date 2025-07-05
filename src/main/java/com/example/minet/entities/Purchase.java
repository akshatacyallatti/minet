package com.example.minet.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn
    private User user;

    @ManyToOne
    private Currency currency;

    private BigDecimal usdSpent;
    private BigDecimal cryptoPurchased;
    private BigDecimal fee;
    private LocalDateTime purchasedAt;
}
