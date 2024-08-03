package ru.otus.hw.service;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StreamsIOServiceTest {
    private static final String NEW_LINE = System.lineSeparator();

    @Test
    void testPrint() {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(outputStream);
        final StreamsIOService ioService = new StreamsIOService(printStream);
        ioService.printLine("Punks Not Dead HOOOIIII");
        assertEquals("Punks Not Dead HOOOIIII" + NEW_LINE, outputStream.toString());
    }

    @Test
    void testPrintFormattedLine() {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(outputStream);
        final StreamsIOService ioService = new StreamsIOService(printStream);
        ioService.printFormattedLine("Name: %s, Age: %d", "Diman Dimanich", 150);
        assertEquals("Name: Diman Dimanich, Age: 150" + NEW_LINE, outputStream.toString());
    }
}
