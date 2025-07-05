package com.example.minet.controllers;

import com.example.minet.dto.CurrencyDetailDto;
import com.example.minet.dto.CurrencyRequestDto;
import com.example.minet.entities.Currency;
import com.example.minet.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> addCurrency(@RequestBody List<CurrencyRequestDto> dtoList) {
        log.info("received request to addCurrency:{}", dtoList);
        Map<String, Object> response = currencyService.addCurrencyList(dtoList);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{currencyId}")
    public ResponseEntity<Currency> updateCurrency(
            @PathVariable Integer currencyId,
            @RequestBody CurrencyRequestDto dto
    ) {
        log.info("received request to updateCurrency:{}, {}", currencyId, dto);
        return ResponseEntity.ok(currencyService.updateCurrency(currencyId, dto));
    }

    @GetMapping("/{currencyId}/details")
    public ResponseEntity<CurrencyDetailDto> getCurrencyDetails(
            @PathVariable Integer currencyId,
            @RequestParam(required = false) String range,
            @RequestParam(required = false) Integer userId
    ) {
        log.info("received request to getCurrencyDetails:{}", currencyId);
        return ResponseEntity.ok(currencyService.getCurrencyDetail(currencyId, range, userId));
    }

}
