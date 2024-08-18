package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    @Mock
    private Student student;

    @InjectMocks
    private TestServiceImpl service;


    @Test
    void testExecuteTestForAllCorrectAnswers() {
        final Question question_1 = new Question("question1", Arrays.asList(new Answer("answer1", Boolean.TRUE), new Answer("answer2", Boolean.FALSE), new Answer("answer3", Boolean.FALSE)));
        final Question question_2 = new Question("question2", Arrays.asList(new Answer("answer1", Boolean.FALSE), new Answer("answer2", Boolean.TRUE), new Answer("answer3", Boolean.FALSE)));
        final Question question_3 = new Question("question3", Arrays.asList(new Answer("answer1", Boolean.FALSE), new Answer("answer2", Boolean.FALSE), new Answer("answer3", Boolean.TRUE)));

        when(questionDao.findAll()).thenReturn(Arrays.asList(question_1, question_2, question_3));

        final AtomicInteger counter = new AtomicInteger(1);
        when(ioService.readIntForRangeWithPrompt(anyInt(), anyInt(), anyString(), anyString()))
                .thenAnswer(invocationOnMock -> {
                    final int andIncrement = counter.getAndIncrement();
                    if (andIncrement <= 3) {
                        return andIncrement;
                    }
                    return null;
                });

        final TestResult testResult = service.executeTestFor(student);
        assertEquals(3, testResult.getAnsweredQuestions().size());
        assertEquals(3, testResult.getRightAnswersCount());
    }

    @Test
    void testExecuteTestForWithoutRightAnswers() {
        final Question question_1 = new Question("question1", Arrays.asList(new Answer("answer1", Boolean.FALSE), new Answer("answer2", Boolean.TRUE), new Answer("answer3", Boolean.FALSE)));
        final Question question_2 = new Question("question2", Arrays.asList(new Answer("answer1", Boolean.FALSE), new Answer("answer2", Boolean.FALSE), new Answer("answer3", Boolean.TRUE)));
        final Question question_3 = new Question("question3", Arrays.asList(new Answer("answer1", Boolean.FALSE), new Answer("answer2", Boolean.TRUE), new Answer("answer3", Boolean.FALSE)));

        when(questionDao.findAll()).thenReturn(Arrays.asList(question_1, question_2, question_3));

        final AtomicInteger counter = new AtomicInteger(1);
        when(ioService.readIntForRangeWithPrompt(anyInt(), anyInt(), anyString(), anyString()))
                .thenAnswer(invocationOnMock -> {
                    final int andIncrement = counter.getAndIncrement();
                    if (andIncrement <= 3) {
                        return andIncrement;
                    }
                    return null;
                });

        final TestResult testResult = service.executeTestFor(student);
        assertEquals(3, testResult.getAnsweredQuestions().size());
        assertEquals(0, testResult.getRightAnswersCount());
    }
}
