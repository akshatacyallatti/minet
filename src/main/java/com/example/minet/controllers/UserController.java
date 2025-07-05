package com.example.minet.controllers;

import com.example.minet.dto.UserDto;
import com.example.minet.dto.UserResponseDto;
import com.example.minet.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody @Valid UserDto userDto) {
        log.info("request received to registerUser {}",userDto);
        UserResponseDto registeredUser = userService.registerUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserDto userDto) {
        log.info("request received to login {}",userDto);
        UserResponseDto response = userService.signIn(userDto.getEmail(), userDto.getPassword());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
