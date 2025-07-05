package com.example.minet.repositories;

import com.example.minet.entities.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {

    List<Purchase> findTop10ByUser_UserIdAndCurrency_CurrencyCodeOrderByPurchasedAtDesc(int userId, String currencyCode);
}

