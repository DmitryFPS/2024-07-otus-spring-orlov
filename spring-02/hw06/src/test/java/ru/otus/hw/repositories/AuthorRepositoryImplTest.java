package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.entity.Author;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jdbc для работы с авторами ")
@DataJpaTest
@Import(AuthorRepositoryImpl.class)
class AuthorRepositoryImplTest {

    @Autowired
    private AuthorRepositoryImpl repository;

    private List<Author> dbAuthors;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
    }


    @DisplayName("Проверка получения всех авторов")
    @Test
    void testFindAll() {
        final List<Author> actualAuthors = repository.findAll();
        final List<Author> authors = dbAuthors;

        assertThat(actualAuthors)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(authors);
        actualAuthors.forEach(System.out::println);
    }

    @DisplayName("Загрузить автора по id")
    @ParameterizedTest
    @MethodSource("getDbAuthors")
    void testFindById(final Author expectedAuthor) {
        final Optional<Author> actualAuthor = repository.findById(expectedAuthor.getId());
        assertThat(actualAuthor)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedAuthor);
    }


    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(Long.valueOf(id), "Author_" + id))
                .toList();
    }
}
