package ru.otus.hw.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final IOService ioService;

    private final CsvQuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        // Получить вопросы из дао и вывести их с вариантами ответов
        final List<Question> questions = questionDao.findAll();
        printQuestions(questions);
    }

    private void printQuestions(@NonNull final List<Question> questions) {
        for (int i = 0; i < questions.size(); i++) {
            final Question question = questions.get(i);
            ioService.printFormattedLine("Question %d:", i + 1);
            ioService.printLine(question.text());
            printAnswers(question);
        }
    }

    private void printAnswers(@NonNull final Question question) {
        final int answersSize = question.answers().size();
        for (int j = 0; j < answersSize; j++) {
            final String textAnswer = ofNullable(question.answers().get(j)).map(Answer::text).orElse(null);
            ioService.printFormattedLine("- %d Answer: %s:", j + 1, textAnswer);
        }
    }
}
