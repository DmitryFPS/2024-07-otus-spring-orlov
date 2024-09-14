package ru.otus.hw.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(final String message) {
        super(message);
    }
}
