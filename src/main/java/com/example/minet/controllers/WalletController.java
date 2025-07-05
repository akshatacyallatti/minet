package com.example.minet.controllers;

import com.example.minet.dto.WalletCreateDto;
import com.example.minet.dto.WalletResponseDto;
import com.example.minet.entities.Wallet;
import com.example.minet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private static final Logger log = LoggerFactory.getLogger(WalletController.class);
    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestBody WalletCreateDto dto) {
        log.info("request received to createWallet:{}",dto);
        Wallet wallet = walletService.createWallet(dto);
        return new ResponseEntity<>(wallet, HttpStatus.CREATED);
    }

    @PatchMapping
    public ResponseEntity<Wallet> updateWalletBalance(@RequestBody WalletCreateDto dto) {
        log.info("request received to updateWalletBalance:{}",dto);
        Wallet wallet = walletService.updateWalletBalance(dto);
        return ResponseEntity.ok(wallet);
    }
    @GetMapping("/{currencyCode}")
    public ResponseEntity<WalletResponseDto> getWalletByCurrency(
            @PathVariable String currencyCode,
            @RequestParam Integer userId) {
        log.info("request received to getWalletByCurrency:{},{}",currencyCode,userId);
        WalletResponseDto dto = walletService.getWalletForUserAndCurrency(userId, currencyCode);
        return ResponseEntity.ok(dto);
    }

}
