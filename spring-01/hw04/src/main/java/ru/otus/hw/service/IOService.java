package ru.otus.hw.service;

public interface IOService {
    void printLine(final String s);

    void printFormattedLine(final String s, final Object... args);

    String readString();

    String readStringWithPrompt(final String prompt);

    int readIntForRange(final int min, final int max, final String errorMessage);

    int readIntForRangeWithPrompt(final int min, final int max, final String prompt, final String errorMessage);
}
