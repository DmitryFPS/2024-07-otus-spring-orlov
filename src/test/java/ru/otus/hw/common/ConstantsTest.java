package ru.otus.hw.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConstantsTest {

    @Test
    void testConstants() {
        assertEquals("%", Constants.PERCENT);
        assertEquals("\\|", Constants.PIPE_SIGN);
        assertEquals(';', Constants.SEMICOLON);
        assertEquals(String.format("%n"), Constants.NEW_LINE);
    }
}