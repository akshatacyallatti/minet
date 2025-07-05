package com.example.minet.repositories;

;
import com.example.minet.entities.Watchlist;
import com.example.minet.entities.User;
import com.example.minet.entities.Currency;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<Watchlist, Integer> {
    Optional<Watchlist> findByUserAndCurrency(User user, Currency currency);

    List<Watchlist> findAllByUser(User user);

    List<Watchlist> findByUser_UserId(Integer userId);

    boolean existsByUser_UserIdAndCurrency_CurrencyId(Integer userId, Integer currencyId);
    List<Watchlist> findAllByUser_UserId(Integer userId);

}