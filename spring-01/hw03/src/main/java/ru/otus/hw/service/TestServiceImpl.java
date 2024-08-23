package ru.otus.hw.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");

        final List<Question> questions = questionDao.findAll();
        final TestResult testResult = new TestResult(student);

        int questionNumber = 1;
        for (final Question question : questions) {
            printQuestion(question, questionNumber);

            final int numberAnswers = question.answers().size();
            final int answerNumber = ioService.readIntForRangeWithPrompt(
                    1, numberAnswers, String.format("Enter the answer number from 1 to %s", numberAnswers),
                    "You entered an incorrect response number");

            final Answer answer = question.answers().get(answerNumber - 1);
            final boolean isAnswerValid = answer.isCorrect(); // Задать вопрос, получить ответ
            testResult.applyAnswer(question, isAnswerValid);
            questionNumber++;
        }
        return testResult;
    }

    private void printQuestion(@NonNull final Question question,
                               final int questionNumber) {
        ioService.printFormattedLine("Question %d:", questionNumber);
        ioService.printLine(question.text());
        printAnswers(question);
    }

    private void printAnswers(@NonNull final Question question) {
        final List<Answer> answers = question.answers();
        for (int j = 0; j < answers.size(); j++) {
            ioService.printFormattedLine("- %d Answer: %s:", j + 1, answers.get(j).text());
        }
    }
}
