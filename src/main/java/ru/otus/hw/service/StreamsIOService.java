package ru.otus.hw.service;

import java.io.PrintStream;

import static ru.otus.hw.common.Constants.NEW_LINE;

public class StreamsIOService implements IOService {
    private final PrintStream printStream;

    public StreamsIOService(final PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void printLine(final String s) {
        printStream.println(s);
    }

    @Override
    public void printFormattedLine(final String s, final Object... args) {
        printStream.printf(s + NEW_LINE, args);
    }
}
