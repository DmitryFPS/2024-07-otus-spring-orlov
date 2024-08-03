package ru.otus.hw.service;

public interface IOService {
    void printLine(final String s);

    void printFormattedLine(final String s, final Object... args);
}
