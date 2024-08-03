package ru.otus.hw.dao;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.junit.jupiter.api.Test;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CsvQuestionDaoTest {
    private static final char SEMICOLON = ';';
    private static final String QUESTIONS_FILE = "questions.csv";
    private static final String TEST = "test.csv";

    @Test
    void testFindSuccess() throws IOException, URISyntaxException {
        final TestFileNameProvider fileNameProvider = mock(TestFileNameProvider.class);
        when(fileNameProvider.getTestFileName()).thenReturn(QUESTIONS_FILE);
        final CsvQuestionDao questionDao = new CsvQuestionDao(fileNameProvider);
        final Path csvFilePath = Paths.get(ClassLoader.getSystemResource(QUESTIONS_FILE).toURI());
        final List<Question> expectedQuestions = readQuestionsFromCsv(csvFilePath);

        final List<Question> actualQuestions = questionDao.findAll();

        assertEquals(expectedQuestions, actualQuestions);
    }

    @Test
    void testFindEmpty() {
        final TestFileNameProvider fileNameProvider = mock(TestFileNameProvider.class);
        when(fileNameProvider.getTestFileName()).thenReturn(TEST);
        final CsvQuestionDao questionDao = new CsvQuestionDao(fileNameProvider);
        assertThrows(QuestionReadException.class, questionDao::findAll);
    }

    @Test
    void testFindInvalid() {
        final TestFileNameProvider fileNameProvider = mock(TestFileNameProvider.class);
        when(fileNameProvider.getTestFileName()).thenReturn(TEST);
        final CsvQuestionDao questionDao = new CsvQuestionDao(fileNameProvider);
        assertThrows(QuestionReadException.class, questionDao::findAll);
    }

    @Test
    void testFindWithInvalidUrl() {
        final TestFileNameProvider fileNameProvider = mock(TestFileNameProvider.class);
        when(fileNameProvider.getTestFileName()).thenReturn(TEST);
        final CsvQuestionDao questionDao = new CsvQuestionDao(fileNameProvider);
        assertThrows(QuestionReadException.class, questionDao::findAll);
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
