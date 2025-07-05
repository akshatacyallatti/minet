package com.example.minet.repositories;

import com.example.minet.entities.Currency;
import com.example.minet.entities.User;
import com.example.minet.entities.Wallet;
import com.example.minet.entities.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    Optional<Wallet> findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(Integer userId, Integer currencyId, String currencyCode);

    Optional<Wallet> findByUser_UserIdAndCurrency_Symbol(Integer userId, String symbol);

    List<Wallet> findByUser_UserId(Integer userId);

    @Query("SELECT w FROM Watchlist w JOIN FETCH w.currency WHERE w.user.userId = :userId")
    List<Watchlist> findByUser_UserIdWithCurrency(@Param("userId") Integer userId);

    Optional<Wallet> findByUser_UserIdAndCurrency_CurrencyCode(Integer userId, String currencyCode);

        Optional<Wallet> findByUserAndCurrency(User user, Currency currency);



}
