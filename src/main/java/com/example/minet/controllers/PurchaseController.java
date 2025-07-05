package com.example.minet.controllers;

import com.example.minet.dto.PurchaseQuoteRequestDto;
import com.example.minet.dto.PurchaseQuoteResponseDto;
import com.example.minet.dto.PurchaseRequestDto;
import com.example.minet.dto.PurchaseResponseDto;
import com.example.minet.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping("/quote")
    public ResponseEntity<PurchaseQuoteResponseDto> getQuote(@RequestBody PurchaseQuoteRequestDto dto) {
        log.info("request received to getQuote {}:",dto);
        return ResponseEntity.ok(purchaseService.getQuote(dto));
    }

    @PostMapping
    public ResponseEntity<PurchaseResponseDto> execute(@RequestBody PurchaseRequestDto dto) {
        log.info("request received to execute {}:",dto);
        return ResponseEntity.ok(purchaseService.executePurchase(dto));
    }
}
