package ru.otus.hw.repositories;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.model.Author;
import ru.otus.hw.model.Book;
import ru.otus.hw.model.Comment;
import ru.otus.hw.model.Genre;
import ru.otus.hw.mongock.events.MongoBookCascadeDeleteEventListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;

@DataMongoTest
@Import({MongoBookCascadeDeleteEventListener.class})
class BookRepositoryImplTest {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MongoOperations mongoOperations;


    @Test
    void testSaveBook() {
        final Author author = mongoOperations.findById(1, Author.class);
        final Genre genre = mongoOperations.findById(1, Genre.class);
        final Book bookToSave = new Book(null, "BookTitleNew", author, Collections.singletonList(genre));
        final Book savedBook = mongoOperations.save(bookToSave);

        assertThat(savedBook).isNotNull()
                .matches(book -> book.getId() != null)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(bookToSave);

        final Book findBook = mongoOperations.findById(savedBook.getId(), Book.class);
        assertThat(findBook)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(savedBook);
    }

    @Test
    void testUpdatedBook() {
        final String updatedTitle = "BookTitleNewNew";
        final Book existingBook = mongoOperations.findById("1", Book.class);
        final Genre additionalGenre = mongoOperations.findById("3", Genre.class);

        final Book bookToUpdate = new Book(existingBook.getId(), updatedTitle, existingBook.getAuthor(),
                Collections.singletonList(additionalGenre));
        final Book updated = bookRepository.save(bookToUpdate);

        final Book book = mongoOperations.findById(updated.getId(), Book.class);
        assertThat(book)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(updated);
    }

    @Test
    void testDeleteBook() {
        final Optional<Book> bookToDelete = bookRepository.findById("1");
        assertThat(bookToDelete).isPresent();

        final List<Comment> allByBookId = commentRepository.findAllByBookId(bookToDelete.get().getId());
        assertThat(allByBookId).isNotEmpty();
        assertThat(commentRepository.findAllByBookId(bookToDelete.get().getId())).isNotNull();

        bookRepository.deleteById("1");

        final Optional<Book> book = bookRepository.findById("1");
        assertThat(book.isPresent()).isFalse();

        final List<Comment> remainingComments = commentRepository.findAllByBookId("1");
        assertThat(remainingComments).isEmpty();
    }

    @Nested
    @DirtiesContext(classMode = BEFORE_CLASS)
    class TestBookRepositoryFind {
        @Test
        void testReturnCorrectBookById() {
            final Book expected = mongoOperations.findById("1", Book.class);
            final Optional<Book> actual = bookRepository.findById("1");
            assertThat(actual).isPresent();
            assertThat(expected)
                    .isNotNull()
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(actual.get());
        }

        @Test
        void testReturnEmptyBookById() {
            final Optional<Book> actual = bookRepository.findById("100");
            assertThat(actual).isNotPresent();
        }

        @Test
        void testReturnCorrectBooksList() {
            final List<Book> actualBooks = bookRepository.findAll();
            final Book book1 = mongoOperations.findById("1", Book.class);
            final Book book2 = mongoOperations.findById("2", Book.class);
            final Book book3 = mongoOperations.findById("3", Book.class);
            final List<Book> expectedBooks = Arrays.asList(book1, book2, book3);

            assertThat(actualBooks)
                    .isNotNull()
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(expectedBooks);
        }
    }
}
