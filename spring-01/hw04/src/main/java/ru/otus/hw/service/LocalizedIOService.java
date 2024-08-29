package ru.otus.hw.service;

public interface LocalizedIOService extends LocalizedMessagesService, IOService {
    void printLineLocalized(final String code);

    void printFormattedLineLocalized(final String code, final Object... args);

    String readStringWithPromptLocalized(final String promptCode);

    int readIntForRangeLocalized(int min, int max, final String errorMessageCode);

    int readInRangeWithPromptLocalized(int min, int max, final String promptCode, final String errorMessageCode);
}
