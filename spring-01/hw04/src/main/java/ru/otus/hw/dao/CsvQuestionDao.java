package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
@Component
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        // Использовать CsvToBean
        // https://opencsv.sourceforge.net/#collection_based_bean_fields_one_to_many_mappings
        // Использовать QuestionReadException
        // Про ресурсы: https://mkyong.com/java/java-read-a-file-from-resources-folder/

        return getQuestions();
    }

    private List<Question> getQuestions() {
        final ClassLoader classLoader = getClass().getClassLoader();
        final String fileName = fileNameProvider.getTestFileName();
        try (final InputStream inputStream = requireNonNull(classLoader.getResourceAsStream(fileName));
             final InputStreamReader reader = new InputStreamReader(inputStream)) {
            return readQuestionsFromCsv(reader);
        } catch (final NullPointerException exc) {
            throw new QuestionReadException("Файл CSV не найден");
        } catch (final IOException exc) {
            throw new QuestionReadException("Произошла ошибка при чтении вопросов из CSV-ресурса", exc);
        }
    }

    private List<Question> readQuestionsFromCsv(final InputStreamReader reader) {
        return new CsvToBeanBuilder<QuestionDto>(reader)
                .withType(QuestionDto.class)
                .withSeparator(';')
                .withSkipLines(1)
                .build().stream()
                .filter(Objects::nonNull)
                .map(QuestionDto::toDomainObject)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
