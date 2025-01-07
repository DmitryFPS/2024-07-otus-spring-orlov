package ru.otus.hw.exceptions;

public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException(final String message) {
        super(message);
    }
}
