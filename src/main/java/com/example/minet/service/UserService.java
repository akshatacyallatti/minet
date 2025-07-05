package com.example.minet.service;

import com.example.minet.dto.UserDto;
import com.example.minet.dto.UserResponseDto;

public interface UserService {
    UserResponseDto registerUser(UserDto userDto);
    UserResponseDto signIn(String email, String password);
}
