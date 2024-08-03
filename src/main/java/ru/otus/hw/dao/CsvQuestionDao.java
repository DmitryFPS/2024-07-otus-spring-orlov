package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
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

import static ru.otus.hw.common.Constants.SEMICOLON;

@RequiredArgsConstructor
@AllArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        // Использовать CsvToBean
        // https://opencsv.sourceforge.net/#collection_based_bean_fields_one_to_many_mappings
        // Использовать QuestionReadException
        // Про ресурсы: https://mkyong.com/java/java-read-a-file-from-resources-folder/

        return getQuestions();
    }

    private List<Question> getQuestions() {
        try {
            final Path path = Paths.get(ClassLoader.getSystemResource(fileNameProvider.getTestFileName()).toURI());
            final List<Question> questions = readQuestionsFromCsv(path);
            if (questions == null || questions.isEmpty()) {
                throw new QuestionReadException("Missing data in CSV");
            }
            return questions;
        } catch (final URISyntaxException exc) {
            throw new QuestionReadException("The URL does not follow the syntax rules", exc);
        }
    }

    private List<Question> readQuestionsFromCsv(final Path filePath) {
        try (final Reader reader = Files.newBufferedReader(filePath)) {
            return new CsvToBeanBuilder<QuestionDto>(reader)
                    .withType(QuestionDto.class)
                    .withSeparator(SEMICOLON)
                    .withSkipLines(1)
                    .build().stream()
                    .filter(Objects::nonNull)
                    .map(QuestionDto::toDomainObject)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (final IOException exc) {
            throw new QuestionReadException("The CSV file with questions and answers was not found", exc);
        }
    }
}
