package ru.otus.hw.service;

public interface LocalizedMessagesService {
    String getMessage(final String code, final Object... args);
}
