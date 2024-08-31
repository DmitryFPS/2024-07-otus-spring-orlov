package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("Репозиторий на основе Jdbc для работы с авторами ")
@JdbcTest
@Import(JdbcAuthorRepository.class)
class JdbcAuthorRepositoryTest {

    @Autowired
    private JdbcAuthorRepository repository;

    @Test
    void testFindAll() {
        final List<Author> authors = repository.findAll();

        assertEquals(1, authors.get(0).getId());
        assertEquals("Author_1", authors.get(0).getFullName());
        assertEquals(2, authors.get(1).getId());
        assertEquals("Author_2", authors.get(1).getFullName());
        assertEquals(3, authors.get(2).getId());
        assertEquals("Author_3", authors.get(2).getFullName());
    }

    @Test
    void testFindById() {
        final Optional<Author> author = repository.findById(1);
        author.ifPresent(value -> assertEquals(1, value.getId()));
        author.ifPresent(value -> assertEquals("Author_1", value.getFullName()));
    }

    @Test
    void testFindByIdException() {
        final Optional<Author> author = repository.findById(Long.MAX_VALUE);
        assertFalse(author.isPresent());
    }
}
