package com.example.minet.controllers;

import com.example.minet.dto.GenericResponse;
import com.example.minet.dto.WatchlistRequestDto;
import com.example.minet.entities.Watchlist;
import com.example.minet.service.WatchlistService;
import com.example.minet.utils.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    @PostMapping
    public ResponseEntity<GenericResponse> toggleWatchlist(@RequestBody WatchlistRequestDto dto) {
        log.info("request received to toggleWatchlist:{}",dto);
        String message = watchlistService.toggleWatchlist(dto);
        return ResponseBuilder.success(message);
    }

    @GetMapping
    public ResponseEntity<List<Watchlist>> getWatchlist(@RequestParam Integer userId) {
        log.info("request received to getWatchlist:{}",userId);
        return ResponseEntity.ok(watchlistService.getWatchlistByUser(userId));
    }
}