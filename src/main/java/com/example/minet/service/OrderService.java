package com.example.minet.service;

import com.example.minet.constants.OrderStatus;
import com.example.minet.constants.OrderType;
import com.example.minet.dto.OrderRequestDto;
import com.example.minet.dto.OrderResponseDto;
import com.example.minet.entities.*;
import com.example.minet.exceptions.DataNotFoundException;
import com.example.minet.exceptions.InvalidRequestException;
import com.example.minet.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final CurrencyRepository currencyRepository;
    private final TradeRepository tradeRepository;

    public List<OrderResponseDto> getOrdersByUserDto(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found with ID: " + userId));

        return orderRepository.findAllByUser(user).stream()
                .map(order -> {
                    OrderResponseDto dto = new OrderResponseDto();
                    dto.setOrderId(order.getOrderId());
                    dto.setOrderType(order.getOrderType());
                    dto.setBalance(order.getBalance());
                    dto.setTotalValue(order.getTotalValue());
                    dto.setStatus(order.getStatus());
                    dto.setCurrencySymbol(order.getCurrency().getSymbol());
                    dto.setCreatedAt(order.getCreatedAt());
                    dto.setUpdatedAt(order.getUpdatedAt());
                    return dto;
                })
                .toList();
    }

    public void placeOrder(OrderRequestDto dto) {
        validateOrderRequest(dto);
        if (dto.getOrderType().equalsIgnoreCase("BUY")) {
            handleBuy(dto);
        } else if (dto.getOrderType().equalsIgnoreCase("SELL")) {
            handleSell(dto);
        } else {
            throw new InvalidRequestException("Invalid order type: " + dto.getOrderType());
        }
    }

    private void validateOrderRequest(OrderRequestDto dto) {
        if (dto == null || dto.getOrderType() == null || dto.getUserId() == null || dto.getCurrencyId() == null
                || dto.getAmount() == null || dto.getAmount() <= 0
                || dto.getPrice() == null || dto.getPrice().doubleValue() <= 0) {
            throw new InvalidRequestException("Invalid order request.");
        }
    }

    private void handleBuy(OrderRequestDto dto) {
        BigDecimal totalCost = BigDecimal.valueOf(dto.getAmount()).multiply(BigDecimal.valueOf(dto.getPrice()));
        log.info("total Cost is :{}",totalCost);
        Wallet usdWallet = walletRepository.findByUser_UserIdAndCurrency_Symbol(dto.getUserId(), "USD")
                .orElseThrow(() -> new DataNotFoundException("USD wallet not found for user ID: " + dto.getUserId()));

        if (usdWallet.getBalance().compareTo(totalCost) < 0) {
            throw new InvalidRequestException("Insufficient USD balance.");
        }

        Wallet cryptoWallet = walletRepository
                .findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(dto.getUserId(), dto.getCurrencyId(), dto.getCurrencyCode())
                .orElseThrow(() -> new DataNotFoundException("Crypto wallet not found. Please create a wallet before placing BUY order."));

        usdWallet.setBalance(usdWallet.getBalance().subtract(totalCost));
        cryptoWallet.setBalance(cryptoWallet.getBalance().add(BigDecimal.valueOf(dto.getAmount())));

        walletRepository.save(usdWallet);
        walletRepository.save(cryptoWallet);

        saveOrder(dto, OrderType.BUY, totalCost);
    }

    private void handleSell(OrderRequestDto dto) {
        BigDecimal amount = BigDecimal.valueOf(dto.getAmount());
        BigDecimal totalValue = amount.multiply(BigDecimal.valueOf(dto.getPrice()));
        log.info("amount:{} and total value is :{}",amount,totalValue);

        Wallet cryptoWallet = walletRepository.findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(
                        dto.getUserId(), dto.getCurrencyId(), dto.getCurrencyCode())
                .orElseThrow(() -> new DataNotFoundException("Crypto wallet not found"));

        if (cryptoWallet.getBalance().compareTo(amount) < 0) {
            throw new InvalidRequestException("Insufficient crypto balance.");
        }

        Wallet usdWallet = walletRepository.findByUser_UserIdAndCurrency_Symbol(dto.getUserId(), "USD")
                .orElseThrow(() -> new DataNotFoundException("USD wallet not found"));

        cryptoWallet.setBalance(cryptoWallet.getBalance().subtract(amount));
        usdWallet.setBalance(usdWallet.getBalance().add(totalValue));

        walletRepository.save(cryptoWallet);
        walletRepository.save(usdWallet);

        saveOrder(dto, OrderType.SELL, totalValue);
    }

    private void saveOrder(OrderRequestDto dto, OrderType orderType, BigDecimal totalValue) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found with ID: " + dto.getUserId()));

        Currency currency = currencyRepository.findById(dto.getCurrencyId())
                .orElseThrow(() -> new DataNotFoundException("Currency not found with ID: " + dto.getCurrencyId()));

        Order order = new Order();
        order.setUser(user);
        order.setCurrency(currency);
        order.setOrderType(orderType);
        order.setBalance(BigDecimal.valueOf(dto.getAmount()));
        order.setTotalValue(totalValue);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);
        matchOrder(order);
    }

    private void matchOrder(Order newOrder) {
        OrderType counterType = newOrder.getOrderType() == OrderType.BUY ? OrderType.SELL : OrderType.BUY;
        log.info("counterType is {}:",counterType);

        List<Order> counterOrders = orderRepository.findByOrderTypeAndCurrency_CurrencyIdAndStatusInOrderByCreatedAtAsc(
                counterType,
                newOrder.getCurrency().getCurrencyId(),
                List.of(OrderStatus.PENDING, OrderStatus.PARTIAL)
        );

        BigDecimal remainingAmount = newOrder.getBalance();
        log.info("remainingAmount is {}:",remainingAmount);

        for (Order counterOrder : counterOrders) {
            if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal counterAmount = counterOrder.getBalance();
            BigDecimal tradedAmount = remainingAmount.min(counterAmount);

            remainingAmount = remainingAmount.subtract(tradedAmount);
            counterAmount = counterAmount.subtract(tradedAmount);

            counterOrder.setBalance(counterAmount);
            counterOrder.setStatus(counterAmount.compareTo(BigDecimal.ZERO) == 0 ? OrderStatus.COMPLETED : OrderStatus.PARTIAL);

            newOrder.setStatus(remainingAmount.compareTo(BigDecimal.ZERO) == 0 ? OrderStatus.COMPLETED : OrderStatus.PARTIAL);

            orderRepository.save(counterOrder);

            Trade trade = new Trade();
            Currency currency = newOrder.getCurrency();
            trade.setCurrency(currency);
            trade.setCurrencyName(currency.getCurrencyName());
            trade.setMarketCap(currency.getMarketCap());
            trade.setChangePercent(currency.getChangePercent());
            trade.setPrice(currency.getPrice());
            trade.setAmount(tradedAmount);
            trade.setCreatedAt(LocalDateTime.now());

            if (newOrder.getOrderType() == OrderType.BUY) {
                trade.setBuyer(newOrder.getUser());
                trade.setSeller(counterOrder.getUser());
            } else {
                trade.setBuyer(counterOrder.getUser());
                trade.setSeller(newOrder.getUser());
            }

            tradeRepository.save(trade);
            log.info("Trade executed: {} units between buyer {} and seller {}",
                    tradedAmount, trade.getBuyer().getUserId(), trade.getSeller().getUserId());
        }

        newOrder.setBalance(remainingAmount);
        orderRepository.save(newOrder);
    }
}
