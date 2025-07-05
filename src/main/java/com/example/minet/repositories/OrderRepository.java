package com.example.minet.repositories;

import com.example.minet.constants.OrderStatus;
import com.example.minet.constants.OrderType;
import com.example.minet.entities.Order;
import com.example.minet.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUser(User user);

    @Query("SELECT o FROM Order o WHERE o.orderType = :orderType " +
            "AND o.currency.currencyId = :currencyId " +
            "AND o.status = 'PENDING' " +
            "ORDER BY o.createdAt ASC")
    List<Order> findMatchingOrder(
            @Param("orderType") OrderType orderType,
            @Param("currencyId") Integer currencyId,
            @Param("totalValue") OrderStatus totalValue
    );
    List<Order> findByOrderTypeAndCurrency_CurrencyIdAndStatusInOrderByCreatedAtAsc(
            OrderType orderType,
            Integer currencyId,
            List<OrderStatus> statuses
    );

}
