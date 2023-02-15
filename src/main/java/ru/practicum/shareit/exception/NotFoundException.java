package ru.practicum.shareit.exception;

import org.springframework.stereotype.Component;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super (message);
    }
}
