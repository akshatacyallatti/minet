package com.example.minet.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "wallets", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "currency_id"})})
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer walletId;

    private BigDecimal balance;
    private LocalDateTime createdAt;
    @Column(name = "currency_name")
    private String currencyName;

    private String currencyCode;
    @ManyToOne
    @JoinColumn(name = "currency_id",unique = true)
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "wallet")
    private List<Transaction> transactions;

}
