package ru.otus.hw.dao;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
class CsvQuestionDaoTest {

    @Autowired
    private CsvQuestionDao service;

    private static final char SEMICOLON = ';';
    private static final String SUCCESS_QUESTIONS_FILE = "questions.csv";

    @Configuration
    static class CsvQuestionDaoConfig {

        @MockBean
        private TestFileNameProvider fileNameProvider;

        @Bean
        CsvQuestionDao csvQuestionDao() {
            when(fileNameProvider.getTestFileName()).thenReturn(SUCCESS_QUESTIONS_FILE);
            return new CsvQuestionDao(fileNameProvider);
        }
    }

    @Test
    void testFindSuccess() throws IOException, URISyntaxException {
        final Path csvFilePath = Paths.get(ClassLoader.getSystemResource(SUCCESS_QUESTIONS_FILE).toURI());
        final List<Question> expectedQuestions = readQuestionsFromCsv(csvFilePath);

        final List<Question> actualQuestions = service.findAll();

        assertEquals(expectedQuestions, actualQuestions);
    }

    @Test
    void testQuestionFileNotFound() {
        boolean isError = false;
        try {
            service.findAll();
        } catch (final QuestionReadException exc) {
            isError = true;
            assertEquals("Файл CSV не найден", exc.getMessage());
        }
        assertTrue(isError);
    }

    private List<Question> readQuestionsFromCsv(final Path filePath) throws IOException {
        try (final Reader reader = Files.newBufferedReader(filePath)) {
            final String[] tests = new String[]{"text", "answers"};
            final ColumnPositionMappingStrategy<QuestionDto> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(QuestionDto.class);
            strategy.setColumnMapping(tests);

            final CsvToBean<QuestionDto> csvToBean = new CsvToBeanBuilder<QuestionDto>(reader)
                    .withType(QuestionDto.class)
                    .withMappingStrategy(strategy)
                    .withSeparator(SEMICOLON)
                    .withSkipLines(1)
                    .build();

            return csvToBean.parse().stream()
                    .filter(Objects::nonNull)
                    .map(QuestionDto::toDomainObject)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }
}
