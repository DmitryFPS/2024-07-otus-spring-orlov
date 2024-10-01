package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoOperations;
import ru.otus.hw.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class GenreRepositoryImplTest {
    @Autowired
    private GenreRepository repository;

    @Autowired
    private MongoOperations mongoOperations;


    @Test
    void testFindAll() {
        final List<Genre> actualGenres = repository.findAll();
        final List<Genre> expectedGenre = mongoOperations.findAll(Genre.class);

        assertThat(actualGenres).isNotNull()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedGenre);
    }

    @ParameterizedTest
    @MethodSource("FindByIdTest")
    void testFindById(final String id) {
        final Optional<Genre> actualGenre = repository.findById(id);
        assertThat(actualGenre).isPresent();
        final Genre expectedGenre = mongoOperations.findById(id, Genre.class);

        assertThat(actualGenre.get())
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedGenre);
    }

    private static Object[] FindByIdTest() {
        return new Object[]{
                new Object[]{"1"},
                new Object[]{"2"},
                new Object[]{"3"},
                new Object[]{"4"},
                new Object[]{"5"}
        };
    }


    @Test
    void testFindAllByIds() {
        final List<Genre> genres = mongoOperations.findAll(Genre.class);
        final Set<String> ids = genres.stream().map(Genre::getId).collect(Collectors.toSet());
        final List<Genre> actualGenre = repository.findAllByIdIn(ids);

        assertThat(actualGenre)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(genres);
    }
}
