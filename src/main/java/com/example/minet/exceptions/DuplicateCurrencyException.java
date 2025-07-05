package com.example.minet.exceptions;

public class DuplicateCurrencyException extends RuntimeException {
    public DuplicateCurrencyException(String message) {
        super(message);
    }
}
