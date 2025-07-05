package com.example.minet.repositories;

import com.example.minet.entities.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Integer> {

    List<PriceHistory> findByCurrency_CurrencyIdAndTimestampAfterOrderByTimestampAsc(Integer currencyId, LocalDateTime from);

}
