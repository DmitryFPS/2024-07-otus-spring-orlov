package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("Репозиторий на основе Jdbc для работы с жанрами ")
@JdbcTest
@Import(JdbcGenreRepository.class)
class JdbcGenreRepositoryTest {

    @Autowired
    private JdbcGenreRepository repository;


    @Test
    void testFindAll() {
        final List<Genre> genres = repository.findAll();

        assertEquals(1, genres.get(0).getId());
        assertEquals("Genre_1", genres.get(0).getName());

        assertEquals(2, genres.get(1).getId());
        assertEquals("Genre_2", genres.get(1).getName());

        assertEquals(3, genres.get(2).getId());
        assertEquals("Genre_3", genres.get(2).getName());

        assertEquals(4, genres.get(3).getId());
        assertEquals("Genre_4", genres.get(3).getName());

        assertEquals(5, genres.get(4).getId());
        assertEquals("Genre_5", genres.get(4).getName());

        assertEquals(6, genres.get(5).getId());
        assertEquals("Genre_6", genres.get(5).getName());
    }

    @Test
    void testFindById() {
        final Optional<Genre> genre = repository.findById(1);
        genre.ifPresent(value -> assertEquals(1, value.getId()));
        genre.ifPresent(value -> assertEquals("Genre_1", value.getName()));
    }

    @Test
    void testFindByIdException() {
        final Optional<Genre> genre = repository.findById(Long.MAX_VALUE);
        assertFalse(genre.isPresent());
    }

    @Test
    void testFindAllByIds() {
        final List<Genre> genres = repository.findAllByIds(Set.of(1L, 2L));

        assertEquals(1, genres.get(0).getId());
        assertEquals("Genre_1", genres.get(0).getName());
        assertEquals(2, genres.get(1).getId());
        assertEquals("Genre_2", genres.get(1).getName());
    }

    @Test
    void testFindAllByIdsEmpty() {
        final List<Genre> genres = repository.findAllByIds(Collections.emptySet());
        assertEquals(0, genres.size());
    }
}
