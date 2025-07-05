package com.example.minet.controllers;

import com.example.minet.dto.TradeResponseDto;
import com.example.minet.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trades")
public class TradeController {

    private final TradeService tradeService;

    @GetMapping
    public ResponseEntity<List<TradeResponseDto>> getFilteredTrades(
            @RequestParam(required = false) String range,
            @RequestParam(required = false) String query) {

        List<TradeResponseDto> trades = tradeService.getFilteredTrades(range, query);
        return ResponseEntity.ok(trades);
    }
}
