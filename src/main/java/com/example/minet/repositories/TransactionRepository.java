package com.example.minet.repositories;

import com.example.minet.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query("SELECT t FROM Transaction t " +
            "WHERE t.currency.currencyId = :currencyId " +
            "AND t.wallet.user.userId = :userId " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findTop10ByCurrencyAndUser(
            @Param("currencyId") int currencyId,
            @Param("userId") int userId
    );

}
