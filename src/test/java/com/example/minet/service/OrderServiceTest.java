package com.example.minet.service;

import com.example.minet.constants.OrderStatus;
import com.example.minet.constants.OrderType;
import com.example.minet.dto.OrderRequestDto;
import com.example.minet.dto.OrderResponseDto;
import com.example.minet.entities.*;
import com.example.minet.entities.Currency;
import com.example.minet.exceptions.DataNotFoundException;
import com.example.minet.exceptions.InvalidRequestException;
import com.example.minet.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private WalletRepository walletRepository;
    @Mock private CurrencyRepository currencyRepository;
    @Mock private TradeRepository tradeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private OrderRequestDto getDto(String type, double amount, double price) {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setUserId(1);
        dto.setCurrencyId(2);
        dto.setCurrencyCode("BTC");
        dto.setOrderType(type);
        dto.setAmount(amount);
        dto.setPrice(price);
        return dto;
    }

    private User getUser(int id) {
        User user = new User();
        user.setUserId(id);
        return user;
    }

    private Currency getCurrency() {
        Currency c = new Currency();
        c.setCurrencyId(2);
        c.setCurrencyName("BTC");
        c.setCurrencyCode("BTC");
        c.setSymbol("₿");
        c.setMarketCap(BigDecimal.TEN);
        c.setChangePercent(1.2);
        c.setPrice(BigDecimal.valueOf(100));
        return c;
    }

    @Test
    void testValidateOrderRequest_NullDto() {
        assertThrows(InvalidRequestException.class, () -> orderService.placeOrder(null));
    }

    @Test
    void testValidateOrderRequest_ZeroAmount() {
        assertThrows(InvalidRequestException.class, () -> orderService.placeOrder(getDto("BUY", 0, 100)));
    }

    @Test
    void testValidateOrderRequest_NegativePrice() {
        assertThrows(InvalidRequestException.class, () -> orderService.placeOrder(getDto("BUY", 1, -10)));
    }

    @Test
    void testInvalidOrderType() {
        assertThrows(InvalidRequestException.class, () -> orderService.placeOrder(getDto("HOLD", 1, 100)));
    }

    @Test
    void testBuyOrder_SuccessWithMatch() {
        OrderRequestDto dto = getDto("BUY", 1, 100);
        User buyer = getUser(1);
        User seller = getUser(2);
        Currency currency = getCurrency();

        Wallet usdWallet = new Wallet();
        usdWallet.setBalance(BigDecimal.valueOf(1000));
        Wallet cryptoWallet = new Wallet();
        cryptoWallet.setBalance(BigDecimal.ZERO);

        Order sellOrder = new Order();
        sellOrder.setUser(seller);
        sellOrder.setCurrency(currency);
        sellOrder.setOrderType(OrderType.SELL);
        sellOrder.setBalance(BigDecimal.ONE);
        sellOrder.setStatus(OrderStatus.PENDING);
        sellOrder.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(1)).thenReturn(Optional.of(buyer));
        when(currencyRepository.findById(2)).thenReturn(Optional.of(currency));
        when(walletRepository.findByUser_UserIdAndCurrency_Symbol(1, "USD")).thenReturn(Optional.of(usdWallet));
        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(1, 2, "BTC"))
                .thenReturn(Optional.of(cryptoWallet));
        when(orderRepository.findByOrderTypeAndCurrency_CurrencyIdAndStatusInOrderByCreatedAtAsc(any(), any(), any()))
                .thenReturn(List.of(sellOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> orderService.placeOrder(dto));
        verify(tradeRepository, times(1)).save(any(Trade.class));
        verify(orderRepository, atLeast(2)).save(any(Order.class));
    }

    @Test
    void testSellOrder_SuccessWithMatch() {
        OrderRequestDto dto = getDto("SELL", 1, 100);
        User seller = getUser(1);
        User buyer = getUser(2);
        Currency currency = getCurrency();

        Wallet cryptoWallet = new Wallet();
        cryptoWallet.setBalance(BigDecimal.valueOf(1));
        Wallet usdWallet = new Wallet();
        usdWallet.setBalance(BigDecimal.ZERO);

        Order buyOrder = new Order();
        buyOrder.setUser(buyer);
        buyOrder.setCurrency(currency);
        buyOrder.setOrderType(OrderType.BUY);
        buyOrder.setBalance(BigDecimal.ONE);
        buyOrder.setStatus(OrderStatus.PENDING);
        buyOrder.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(1)).thenReturn(Optional.of(seller));
        when(currencyRepository.findById(2)).thenReturn(Optional.of(currency));
        when(walletRepository.findByUser_UserIdAndCurrency_Symbol(1, "USD")).thenReturn(Optional.of(usdWallet));
        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(1, 2, "BTC"))
                .thenReturn(Optional.of(cryptoWallet));
        when(orderRepository.findByOrderTypeAndCurrency_CurrencyIdAndStatusInOrderByCreatedAtAsc(any(), any(), any()))
                .thenReturn(List.of(buyOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> orderService.placeOrder(dto));
        verify(tradeRepository, times(1)).save(any(Trade.class));
    }

    @Test
    void testGetOrdersByUserDto_UserNotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(DataNotFoundException.class, () -> orderService.getOrdersByUserDto(999));
    }

    @Test
    void testGetOrdersByUserDto_Success() {
        User user = getUser(1);
        Currency currency = getCurrency();
        Order order = new Order();
        order.setOrderId(1);
        order.setUser(user);
        order.setCurrency(currency);
        order.setOrderType(OrderType.BUY);
        order.setBalance(BigDecimal.ONE);
        order.setTotalValue(BigDecimal.TEN);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(orderRepository.findAllByUser(user)).thenReturn(List.of(order));

        List<OrderResponseDto> result = orderService.getOrdersByUserDto(1);
        assertEquals(1, result.size());
        assertEquals(order.getOrderId(), result.get(0).getOrderId());
    }

    @Test
    void testMatchOrder_NoMatchFound() {
        OrderRequestDto dto = getDto("BUY", 1, 100);
        User user = getUser(1);
        Currency currency = getCurrency();

        Wallet usdWallet = new Wallet();
        usdWallet.setBalance(BigDecimal.valueOf(1000));
        Wallet cryptoWallet = new Wallet();
        cryptoWallet.setBalance(BigDecimal.ZERO);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(currencyRepository.findById(2)).thenReturn(Optional.of(currency));
        when(walletRepository.findByUser_UserIdAndCurrency_Symbol(1, "USD")).thenReturn(Optional.of(usdWallet));
        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(1, 2, "BTC"))
                .thenReturn(Optional.of(cryptoWallet));
        when(orderRepository.findByOrderTypeAndCurrency_CurrencyIdAndStatusInOrderByCreatedAtAsc(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> orderService.placeOrder(dto));
        verify(tradeRepository, never()).save(any());
    }
    @Test
    void testBuyOrder_UsdWalletNotFound() {
        OrderRequestDto dto = getDto("BUY", 1, 100);
        when(walletRepository.findByUser_UserIdAndCurrency_Symbol(1, "USD")).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> orderService.placeOrder(dto));
    }
    @Test
    void testBuyOrder_CryptoWalletNotFound() {
        OrderRequestDto dto = getDto("BUY", 1, 100);
        Wallet usdWallet = new Wallet(); usdWallet.setBalance(BigDecimal.valueOf(1000));

        when(walletRepository.findByUser_UserIdAndCurrency_Symbol(1, "USD")).thenReturn(Optional.of(usdWallet));
        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(1, 2, "BTC"))
                .thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> orderService.placeOrder(dto));
    }
    @Test
    void testBuyOrder_InsufficientUsdBalance() {
        OrderRequestDto dto = getDto("BUY", 1, 100);
        Wallet usdWallet = new Wallet(); usdWallet.setBalance(BigDecimal.valueOf(10)); // insufficient

        when(walletRepository.findByUser_UserIdAndCurrency_Symbol(1, "USD")).thenReturn(Optional.of(usdWallet));
        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(1, 2, "BTC"))
                .thenReturn(Optional.of(new Wallet()));

        assertThrows(InvalidRequestException.class, () -> orderService.placeOrder(dto));
    }
    @Test
    void testSellOrder_CryptoWalletNotFound() {
        OrderRequestDto dto = getDto("SELL", 1, 100);

        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(1, 2, "BTC"))
                .thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> orderService.placeOrder(dto));
    }
    @Test
    void testSellOrder_UsdWalletNotFound() {
        OrderRequestDto dto = getDto("SELL", 1, 100);
        Wallet cryptoWallet = new Wallet(); cryptoWallet.setBalance(BigDecimal.valueOf(1));

        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(1, 2, "BTC"))
                .thenReturn(Optional.of(cryptoWallet));
        when(walletRepository.findByUser_UserIdAndCurrency_Symbol(1, "USD"))
                .thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> orderService.placeOrder(dto));
    }
    @Test
    void testSellOrder_InsufficientCryptoBalance() {
        OrderRequestDto dto = getDto("SELL", 1, 100);
        Wallet cryptoWallet = new Wallet(); cryptoWallet.setBalance(BigDecimal.valueOf(0)); // zero balance

        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(1, 2, "BTC"))
                .thenReturn(Optional.of(cryptoWallet));

        assertThrows(InvalidRequestException.class, () -> orderService.placeOrder(dto));
    }
    @Test
    void testSaveOrder_CurrencyNotFound() {
        OrderRequestDto dto = getDto("BUY", 1, 100);
        User user = getUser(1);
        Wallet usdWallet = new Wallet(); usdWallet.setBalance(BigDecimal.valueOf(1000));
        Wallet cryptoWallet = new Wallet(); cryptoWallet.setBalance(BigDecimal.ZERO);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(walletRepository.findByUser_UserIdAndCurrency_Symbol(1, "USD")).thenReturn(Optional.of(usdWallet));
        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(1, 2, "BTC"))
                .thenReturn(Optional.of(cryptoWallet));
        when(currencyRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> orderService.placeOrder(dto));
    }

}
