package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoOperations;
import ru.otus.hw.model.Author;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class AuthorRepositoryImplTest {
    @Autowired
    private AuthorRepository repository;

    @Autowired
    private MongoOperations mongoOperations;


    @Test
    void testFindAll() {
        final List<Author> actualAuthors = repository.findAll();
        final List<Author> authors = mongoOperations.findAll(Author.class);

        assertThat(actualAuthors)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(authors);
    }

    @ParameterizedTest
    @MethodSource("FindByIdTest")
    void testFindById(final String id) {
        final Optional<Author> actualAuthor = repository.findById(id);
        final Author expectedAuthor = mongoOperations.findById(id, Author.class);

        assertThat(actualAuthor).isPresent();
        assertThat(actualAuthor.get())
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedAuthor);
    }

    private static Object[] FindByIdTest() {
        return new Object[]{
                new Object[]{"1"},
                new Object[]{"2"},
                new Object[]{"3"}
        };
    }
}
