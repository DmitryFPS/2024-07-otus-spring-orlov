package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.entity.Author;
import ru.otus.hw.entity.Book;
import ru.otus.hw.entity.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jdbc для работы с книгами ")
@DataJpaTest
class BookRepositoryImplTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private GenreRepository genreRepository;

    private List<Author> dbAuthors;

    private List<Genre> dbGenres;

    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
    }

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectBookById(final Book expectedBook) {
        final Optional<Book> actualBook = bookRepository.findById(expectedBook.getId());
        assertThat(actualBook)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        final List<Book> actualBooks = bookRepository.findAll();
        final List<Book> expectedBooks = dbBooks;
        assertThat(actualBooks)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedBooks);
        actualBooks.forEach(System.out::println);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        final List<Genre> allByIds = genreRepository.findAllByIdIn(Set.of(0L, 2L));
        final Book expectedBook = new Book(null, "BookTitleNew", dbAuthors.get(0), allByIds);
        final Book returnedBook = bookRepository.save(expectedBook);

        assertThat(returnedBook).isNotNull();
        assertThat(returnedBook.getId()).isGreaterThan(0);
        assertThat(returnedBook.getTitle()).isEqualTo("BookTitleNew");
        assertThat(returnedBook.getAuthor()).isEqualTo(dbAuthors.get(0));
        assertThat(returnedBook.getGenres()).containsExactlyInAnyOrderElementsOf(allByIds);

        assertThat(returnedBook)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        final Book expectedBook = new Book(1L, "BookTitleUpdate", dbAuthors.get(2),
                List.of(dbGenres.get(4), dbGenres.get(5)));

        assertThat(bookRepository.findById(expectedBook.getId()))
                .isPresent()
                .get()
                .isNotEqualTo(expectedBook);

        final Book returnedBook = bookRepository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(bookRepository.findById(returnedBook.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен удалять книгу по id")
    @Test
    void shouldDeleteBook() {
        assertThat(bookRepository.findById(1L)).isPresent();
        bookRepository.deleteById(1L);
        assertThat(bookRepository.findById(1L)).isEmpty();
    }

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(Long.valueOf(id), "Author_" + id))
                .toList();
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(Long.valueOf(id), "Genre_" + id))
                .toList();
    }

    private static List<Book> getDbBooks(final List<Author> dbAuthors,
                                         final List<Genre> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Book(Long.valueOf(id),
                        "BookTitle_" + id,
                        dbAuthors.get(id - 1),
                        dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)
                ))
                .toList();
    }

    private static List<Book> getDbBooks() {
        final List<Author> dbAuthors = getDbAuthors();
        final List<Genre> dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }
}
