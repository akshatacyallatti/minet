package com.example.minet.controllers;

import com.example.minet.dto.GenericResponse;
import com.example.minet.dto.OrderRequestDto;
import com.example.minet.dto.OrderResponseDto;
import com.example.minet.service.OrderService;
import com.example.minet.utils.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUser(@PathVariable Integer userId) {
        log.info("received request to getOrdersByUser:{}",userId);
        List<OrderResponseDto> response = orderService.getOrdersByUserDto(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/place")
    public ResponseEntity<GenericResponse> placeOrder(@RequestBody @Valid OrderRequestDto dto) {
        log.info("received request to placeOrder:{}",dto);
        orderService.placeOrder(dto);
        return ResponseBuilder.success("order placed successfully.");
    }


}
