package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.entity.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jdbc для работы с жанрами ")
@DataJpaTest
class GenreRepositoryImplTest {

    @Autowired
    private GenreRepository repository;

    private List<Genre> dbGenres;


    @BeforeEach
    void setUp() {
        dbGenres = getDbGenres();
    }


    @DisplayName("Проверить получение всех жанров")
    @Test
    void testFindAll() {
        final List<Genre> actualGenres = repository.findAll();
        final List<Genre> expectedGenre = dbGenres;

        assertThat(actualGenres).isNotNull()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedGenre);
    }

    @DisplayName("Проверить получение жанра по id")
    @ParameterizedTest
    @MethodSource("getDbGenres")
    void testFindById(final Genre expectedGenre) {
        final Optional<Genre> actualGenre = repository.findById(expectedGenre.getId());

        assertThat(actualGenre)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedGenre);
    }

    @DisplayName("Проверить получение жанров по списку id")
    @Test
    void testFindAllByIds() {
        final Set<Long> ids = dbGenres.stream().map(Genre::getId).collect(Collectors.toSet());
        final List<Genre> expectedGenre = dbGenres;
        final List<Genre> actualGenre = repository.findAllByIdIn(ids);

        assertThat(actualGenre)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedGenre);
    }


    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(Long.valueOf(id), "Genre_" + id))
                .toList();
    }
}
