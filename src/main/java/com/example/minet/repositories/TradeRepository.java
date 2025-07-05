package com.example.minet.repositories;

import com.example.minet.entities.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Integer> {
    @Query("SELECT t FROM Trade t WHERE " + "(:query IS NULL OR LOWER(t.currencyName) LIKE LOWER(CONCAT('%', :query, '%'))) " + "AND (:fromTime IS NULL OR t.createdAt >= :fromTime)")
    List<Trade> findFilteredTrades(@Param("query") String query, @Param("fromTime") LocalDateTime fromTime);


    List<Trade> findByCurrencyNameContainingIgnoreCaseAndCreatedAtAfter(String currencyName, LocalDateTime after);

    List<Trade> findByCurrencyNameContainingIgnoreCase(String currencyName);

    List<Trade> findByCreatedAtAfter(LocalDateTime after);

    List<Trade> findByBuyer_UserIdOrSeller_UserId(Integer buyerId, Integer sellerId);
}