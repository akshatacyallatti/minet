package com.example.minet.controllers;

import com.example.minet.dto.DashboardResponseDto;
import com.example.minet.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/{userId}")
    public ResponseEntity<DashboardResponseDto> getDashboard(@PathVariable Integer userId) {
        log.info("received request to getDashboard:{}",userId);
        DashboardResponseDto response = dashboardService.getDashboardForUser(userId);
        return ResponseEntity.ok(response);
    }
}
