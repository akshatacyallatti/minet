package com.example.minet.service;

import com.example.minet.dto.*;
import com.example.minet.entities.*;
import com.example.minet.exceptions.DataNotFoundException;
import com.example.minet.exceptions.InvalidRequestException;
import com.example.minet.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final CurrencyRepository currencyRepository;
    private final WalletRepository walletRepository;
    private final AppLogRepository logRepository;
    private final PurchaseRepository purchaseRepository;

    public PurchaseQuoteResponseDto getQuote(PurchaseQuoteRequestDto dto) {
        Currency currency = currencyRepository.findById(dto.getCurrencyId())
                .orElseThrow(() -> new DataNotFoundException("Currency not found with ID: " + dto.getCurrencyId()));

        if (dto.getAmountInUsd() == null || dto.getAmountInUsd().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidRequestException("Amount in USD must be greater than zero.");
        }

        BigDecimal rate = currency.getPrice();
        BigDecimal fee = new BigDecimal("0.001");
        BigDecimal cryptoAmount = dto.getAmountInUsd().divide(rate, 8, RoundingMode.HALF_UP);
        BigDecimal totalUsd = dto.getAmountInUsd().add(fee.multiply(rate));

        PurchaseQuoteResponseDto response = new PurchaseQuoteResponseDto();
        response.setCryptoAmount(cryptoAmount);
        response.setRate(rate);
        response.setFee(fee);
        response.setTotalUsd(totalUsd);

        return response;
    }

    public PurchaseResponseDto executePurchase(PurchaseRequestDto dto) {
        Currency currency = currencyRepository.findById(dto.getCurrencyId())
                .orElseThrow(() -> new DataNotFoundException("Currency not found with ID: " + dto.getCurrencyId()));

        if (dto.getAmountInUsd() == null || dto.getAmountInUsd().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidRequestException("Purchase amount must be greater than zero.");
        }

        BigDecimal rate = currency.getPrice();
        BigDecimal fee = new BigDecimal("0.001");
        BigDecimal cryptoAmount = dto.getAmountInUsd().divide(rate, 8, RoundingMode.HALF_UP);
        BigDecimal totalCost = dto.getAmountInUsd().add(fee.multiply(rate));

        Wallet usdWallet = walletRepository.findByUser_UserIdAndCurrency_CurrencyCode(dto.getUserId(), "USD")
                .orElseThrow(() -> new DataNotFoundException("USD wallet not found for user ID: " + dto.getUserId()));

        if (usdWallet.getBalance().compareTo(totalCost) < 0) {
            throw new InvalidRequestException("Insufficient USD balance to complete purchase.");
        }

        // Deduct from USD wallet
        usdWallet.setBalance(usdWallet.getBalance().subtract(totalCost));
        walletRepository.save(usdWallet);

        // Fetch or create the crypto wallet safely
        Wallet cryptoWallet = walletRepository
                .findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(dto.getUserId(), dto.getCurrencyId(), dto.getCurrencyCode())
                .orElse(null);

        if (cryptoWallet == null) {
            cryptoWallet = new Wallet();
            User user = new User();
            user.setUserId(dto.getUserId());
            cryptoWallet.setUser(user);
            cryptoWallet.setCurrency(currency);
            cryptoWallet.setBalance(BigDecimal.ZERO);
            cryptoWallet.setCreatedAt(LocalDateTime.now());
            cryptoWallet.setCurrencyCode(currency.getCurrencyCode());
            cryptoWallet.setCurrencyName(currency.getCurrencyName());
        }

        // Update crypto wallet balance
        cryptoWallet.setBalance(cryptoWallet.getBalance().add(cryptoAmount));
        walletRepository.save(cryptoWallet);

        // Log the transaction for validation purpose
        AppLog log = new AppLog();
        log.setUserId(dto.getUserId());
        log.setAction("PURCHASE");
        log.setDetails("Purchased " + cryptoAmount + " " + currency.getCurrencyCode() + " for $" + totalCost);
        log.setTimestamp(LocalDateTime.now());
        logRepository.save(log);

        Purchase purchase = new Purchase();
        purchase.setUser(usdWallet.getUser());
        purchase.setCurrency(currency);
        purchase.setUsdSpent(totalCost);
        purchase.setFee(fee);
        purchase.setPurchasedAt(LocalDateTime.now());
        purchaseRepository.save(purchase);


        PurchaseResponseDto response = new PurchaseResponseDto();
        response.setStatus("SUCCESS");
        response.setMessage("Purchase completed please check your balance in your crypto wallet");
        response.setCurrencySymbol(currency.getCurrencyCode());
        response.setCryptoAmount(cryptoAmount);
        response.setUsdSpent(totalCost);
        response.setFee(fee);
        response.setPurchasedAt(LocalDateTime.now());

        return response;
    }
}
