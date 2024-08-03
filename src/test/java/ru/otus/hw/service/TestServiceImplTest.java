package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TestServiceImplTest {
    @Test
    void testExecuteTestNoQuestions() {
        final IOService mockIOService = Mockito.mock(IOService.class);
        final CsvQuestionDao mockQuestionDao = Mockito.mock(CsvQuestionDao.class);
        final TestServiceImpl testService = new TestServiceImpl(mockIOService, mockQuestionDao);
        when(mockQuestionDao.findAll()).thenReturn(null);
        testService.executeTest();

        verify(mockIOService).printLine("");
        verify(mockIOService).printFormattedLine("Please answer the questions below%n");
        verify(mockIOService, Mockito.never()).printFormattedLine("Question 1:", 1);
        verify(mockIOService, Mockito.never()).printLine("Вопрос 1");
        verify(mockIOService, Mockito.never()).printFormattedLine("- 1 Answer: Ответ 1:", 1, "Ответ 1");
        verify(mockIOService, Mockito.never()).printFormattedLine("- 2 Answer: Ответ 2:", 2, "Ответ 2");
    }

    @Test
    void testExecuteTestWhenQuestions() {
        final IOService mockIOService = Mockito.mock(IOService.class);
        final CsvQuestionDao mockQuestionDao = Mockito.mock(CsvQuestionDao.class);
        final TestServiceImpl testService = new TestServiceImpl(mockIOService, mockQuestionDao);
        final Question question1 = new Question("Вопрос 1", List.of(new Answer("Ответ 1", Boolean.TRUE), new Answer("Ответ 2", Boolean.TRUE)));
        final Question question2 = new Question("Вопрос 2", List.of(new Answer("Ответ 3", Boolean.TRUE), new Answer("Ответ 4", Boolean.TRUE)));
        final List<Question> questions = new ArrayList<>(List.of(question1, question2));
        when(mockQuestionDao.findAll()).thenReturn(questions);

        testService.executeTest();

        verify(mockIOService).printLine("");
        verify(mockIOService).printFormattedLine("Please answer the questions below%n");
        verify(mockIOService).printLine("Вопрос 1");
        verify(mockIOService).printLine("Вопрос 2");
    }
}
