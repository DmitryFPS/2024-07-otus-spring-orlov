package ru.otus.hw.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

@Service
public class StreamsIOService implements IOService {
    private static final int MAX_ATTEMPTS = 10;

    private final PrintStream printStream;

    private final Scanner scanner;

    public StreamsIOService(@Value("#{T(System).out}") final PrintStream printStream,
                            @Value("#{T(System).in}") final InputStream inputStream) {

        this.printStream = printStream;
        this.scanner = new Scanner(inputStream);
    }

    @Override
    public void printLine(final String s) {
        printStream.println(s);
    }

    @Override
    public void printFormattedLine(final String s, final Object... args) {
        printStream.printf(s + "%n", args);
    }

    @Override
    public final String readString() {
        return scanner.nextLine();
    }

    @Override
    public String readStringWithPrompt(final String prompt) {
        printLine(prompt);
        return scanner.nextLine();
    }

    @Override
    public int readIntForRange(final int min, final int max, final String errorMessage) {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            try {
                final String stringValue = scanner.nextLine();
                final int intValue = Integer.parseInt(stringValue);
                if (intValue < min || intValue > max) {
                    throw new IllegalArgumentException();
                }
                return intValue;
            } catch (final IllegalArgumentException e) {
                printLine(errorMessage);
            }
        }
        throw new IllegalArgumentException("Error during reading int value");
    }

    @Override
    public int readIntForRangeWithPrompt(final int min, final int max, final String prompt, final String errorMessage) {
        printLine(prompt);
        return readIntForRange(min, max, errorMessage);
    }
}
