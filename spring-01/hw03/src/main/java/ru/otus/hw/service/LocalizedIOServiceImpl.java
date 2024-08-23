package ru.otus.hw.service;

import org.springframework.stereotype.Service;

@Service
public class LocalizedIOServiceImpl implements LocalizedIOService {

    private final LocalizedMessagesService localizedMessagesService;

    private final IOService ioService;

    public LocalizedIOServiceImpl(final LocalizedMessagesService localizedMessagesService,
                                  final IOService ioService) {
        this.localizedMessagesService = localizedMessagesService;
        this.ioService = ioService;
    }

    @Override
    public void printLine(final String s) {
        ioService.printLine(s);
    }

    @Override
    public void printFormattedLine(final String s, final Object... args) {
        ioService.printFormattedLine(s, args);
    }

    @Override
    public String readString() {
        return ioService.readString();
    }

    @Override
    public String readStringWithPrompt(final String prompt) {
        return ioService.readStringWithPrompt(prompt);
    }

    @Override
    public int readIntForRange(final int min, final int max, final String errorMessage) {
        return ioService.readIntForRange(min, max, errorMessage);
    }

    @Override
    public int readIntForRangeWithPrompt(final int min, final int max, final String prompt, final String errorMessage) {
        return ioService.readIntForRangeWithPrompt(min, max, prompt, errorMessage);
    }

    @Override
    public void printLineLocalized(final String code) {
        ioService.printLine(localizedMessagesService.getMessage(code));
    }

    @Override
    public void printFormattedLineLocalized(final String code, final Object... args) {
        ioService.printLine(localizedMessagesService.getMessage(code, args));
    }

    @Override
    public String readStringWithPromptLocalized(final String promptCode) {
        return ioService.readStringWithPrompt(localizedMessagesService.getMessage(promptCode));
    }

    @Override
    public int readIntForRangeLocalized(int min, int max, final String errorMessageCode) {
        return ioService.readIntForRange(min, max, localizedMessagesService.getMessage(errorMessageCode));
    }

    @Override
    public int readInRangeWithPromptLocalized(int min, int max, final String promptCode, final String errorMessageCode) {
        return ioService.readIntForRangeWithPrompt(min, max,
                localizedMessagesService.getMessage(promptCode),
                localizedMessagesService.getMessage(errorMessageCode)
        );
    }

    @Override
    public String getMessage(final String code, final Object... args) {
        return localizedMessagesService.getMessage(code, args);
    }
}
