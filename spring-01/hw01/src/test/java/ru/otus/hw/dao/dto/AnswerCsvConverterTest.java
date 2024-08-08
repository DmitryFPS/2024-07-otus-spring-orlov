package ru.otus.hw.dao.dto;

import org.junit.jupiter.api.Test;
import ru.otus.hw.domain.Answer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AnswerCsvConverterTest {
    private final AnswerCsvConverter converter = new AnswerCsvConverter();

    @Test
    void testConvertToRead() {
        final String csvValue = "Answer text%true";
        final Answer expectedAnswer = new Answer("Answer text", true);

        final Answer actualAnswer = (Answer) converter.convertToRead(csvValue);

        assertEquals(expectedAnswer, actualAnswer);
    }

    @Test
    void testConvertToReadThrows() {
        final String csvValue = "Answer text";
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> converter.convertToRead(csvValue));
    }
}
