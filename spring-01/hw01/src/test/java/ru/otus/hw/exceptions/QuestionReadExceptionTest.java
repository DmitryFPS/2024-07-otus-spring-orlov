package ru.otus.hw.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionReadExceptionTest {
    @Test
    void testQuestionReadThrowable() {
        final Throwable cause = new Exception("Test Throwable");
        final QuestionReadException exception = new QuestionReadException("Test message", cause);
        assertEquals("Test message", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testQuestionReadExceptionWithMessage() {
        final QuestionReadException exception = new QuestionReadException("Test message");
        assertEquals("Test message", exception.getMessage());
        assertNull(exception.getCause());
    }
}
