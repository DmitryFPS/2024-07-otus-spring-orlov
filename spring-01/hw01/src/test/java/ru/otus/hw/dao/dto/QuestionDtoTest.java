package ru.otus.hw.dao.dto;

import org.junit.jupiter.api.Test;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuestionDtoTest {
    @Test
    void testToDomainObject() {
        final String text = "Лучший язык программирования?";
        final List<Answer> answers = new ArrayList<>();
        answers.add(new Answer("Java", true));
        answers.add(new Answer("Golang", false));

        final QuestionDto questionDto = new QuestionDto();
        questionDto.setText(text);
        questionDto.setAnswers(answers);

        final Question question = questionDto.toDomainObject();

        assertEquals(text, question.text());
        assertEquals(answers, question.answers());
    }

    @Test
    void testToDomain() {
        final String text = "Вопрос вопросов";
        final QuestionDto questionDto = new QuestionDto();
        questionDto.setText(text);
        questionDto.setAnswers(new ArrayList<>());

        final Question question = questionDto.toDomainObject();

        assertEquals(text, question.text());
        assertEquals(0, question.answers().size());
    }
}