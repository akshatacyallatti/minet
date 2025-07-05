package com.example.minet.service;

import com.example.minet.dto.WalletCreateDto;
import com.example.minet.dto.WalletResponseDto;
import com.example.minet.entities.*;
import com.example.minet.exceptions.DataNotFoundException;
import com.example.minet.exceptions.DuplicateWalletException;
import com.example.minet.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final CurrencyRepository currencyRepository;
    private  final PurchaseRepository purchaseRepository;

    public Wallet createWallet(WalletCreateDto dto) {
        walletRepository.findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(dto.getUserId(), dto.getCurrencyId(), dto.getCurrencyCode())
                .ifPresent(wallet -> {
                    throw new DuplicateWalletException("Wallet for this currency already exists for the user.");
                });

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found with ID: " + dto.getUserId()));

        Currency currency = currencyRepository.findById(dto.getCurrencyId())
                .orElseThrow(() -> new DataNotFoundException("Currency not found with ID: " + dto.getCurrencyId()));

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setCurrency(currency);
        wallet.setBalance(dto.getBalance());
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setCurrencyCode(dto.getCurrencyCode());
        wallet.setCurrencyName(currency.getCurrencyName());

        return walletRepository.save(wallet);
    }

    public Wallet updateWalletBalance(WalletCreateDto dto) {
        Wallet wallet = walletRepository.findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(dto.getUserId(), dto.getCurrencyId(),dto.getCurrencyCode())
                .orElseThrow(() -> new DataNotFoundException("Wallet not found for user ID: " + dto.getUserId() + " and currency ID: " + dto.getCurrencyId()));

        wallet.setBalance(dto.getBalance());
        return walletRepository.save(wallet);
    }

    public WalletResponseDto getWalletForUserAndCurrency(int userId, String currencyCode) {
        Currency currency = currencyRepository.findByCurrencyCode(currencyCode)
                .orElseThrow(() -> new DataNotFoundException("Currency not found"));

        Wallet wallet = walletRepository.findByUser_UserIdAndCurrency_CurrencyCode(userId, currencyCode)
                .orElseThrow(() -> new DataNotFoundException("Wallet not found for currency: " + currencyCode));

        BigDecimal balance = wallet.getBalance();

        List<Purchase> purchases = purchaseRepository
                .findTop10ByUser_UserIdAndCurrency_CurrencyCodeOrderByPurchasedAtDesc(userId, currencyCode);

        List<WalletResponseDto.TransactionItem> txItems = purchases.stream().map(purchase -> {
            WalletResponseDto.TransactionItem item = new WalletResponseDto.TransactionItem();
            item.setAmount(purchase.getCryptoPurchased());
            return item;
        }).toList();

        WalletResponseDto dto = new WalletResponseDto();
        dto.setCurrencyId(currency.getCurrencyId());
        dto.setCurrencyName(currency.getCurrencyName());
        dto.setCurrencyCode(currency.getCurrencyCode());
        dto.setSymbol(currency.getSymbol());
        dto.setCurrentPrice(currency.getPrice());
        dto.setMarketCap(currency.getMarketCap());
        dto.setCirculatingSupply(BigDecimal.ZERO);
        dto.setTotalBalance(balance);
        dto.setTransactions(txItems);

        return dto;
    }


}
