package ru.otus.hw.exceptions;

public class QuestionReadException extends RuntimeException {
    public QuestionReadException(final String message, final Throwable ex) {
        super(message, ex);
    }

    public QuestionReadException(final String message) {
        super(message);
    }
}
