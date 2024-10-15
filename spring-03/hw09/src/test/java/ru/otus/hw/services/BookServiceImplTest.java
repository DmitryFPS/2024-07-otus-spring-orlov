package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mapper.BookMapperImpl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Service для работы с Books")
@DataJpaTest
@Import({BookMapperImpl.class, BookServiceImpl.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class BookServiceImplTest {
    @Autowired
    private BookServiceImpl serviceTest;

    private List<AuthorDto> dbAuthors;

    private List<GenreDto> dbGenres;

    private List<BookDto> dbBooks;


    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
    }


    @DisplayName("Проверяем получение книги по id")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void testGetBookById(final BookDto expectedBook) {
        final BookDto actualBook = serviceTest.findById(expectedBook.getId());
        assertThat(actualBook).isEqualTo(expectedBook);
    }

    @DisplayName("Проверяем получение списка всех книг")
    @Test
    void testGetBooksList() {
        final List<BookDto> actualBooks = serviceTest.findAll();
        final List<BookDto> expectedBooks = dbBooks;
        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);
    }

    @DisplayName("Проверяем сохранение новой книги")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testSaveNewBook() {
        final BookDto expectedBook = new BookDto(null, "BookTitleNew", dbAuthors.get(0),
                List.of(dbGenres.get(0), dbGenres.get(2)));

        final BookCreateDto bookCreateDto = new BookCreateDto(expectedBook.getTitle(), expectedBook.getAuthor().getId(),
                expectedBook.getGenres().stream().map(GenreDto::getId).collect(Collectors.toSet()));

        final BookDto returnedBook = serviceTest.create(bookCreateDto);

        assertThat(returnedBook)
                .isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedBook);
    }

    @DisplayName("Проверяем обновление книги")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testUpdatedBook() {
        final BookDto expectedBook = new BookDto(1L, "BookTitleUpdate", dbAuthors.get(2),
                List.of(dbGenres.get(4), dbGenres.get(5)));

        final BookUpdateDto bookUpdateDto = new BookUpdateDto(expectedBook.getId(),
                expectedBook.getTitle(), expectedBook.getAuthor().getId(),
                expectedBook.getGenres().stream().map(GenreDto::getId).collect(Collectors.toSet()));
        final BookDto returnedBook = serviceTest.update(bookUpdateDto);

        assertThat(returnedBook)
                .isNotNull()
                .isEqualTo(expectedBook);
    }

    @DisplayName("Проверка удаления книги")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testDeleteBook() {
        final BookDto book = serviceTest.findById(1L);
        serviceTest.deleteById(book.getId());
        assertThatThrownBy(() -> serviceTest.findById(1L))
                .isInstanceOf(NotFoundException.class);
    }

    private static List<AuthorDto> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new AuthorDto(Long.valueOf(id), "Author_" + id))
                .toList();
    }

    private static List<GenreDto> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new GenreDto(Long.valueOf(id), "Genre_" + id))
                .toList();
    }

    private static List<BookDto> getDbBooks(List<AuthorDto> dbAuthors, List<GenreDto> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new BookDto(Long.valueOf(id),
                        "BookTitle_" + id,
                        dbAuthors.get(id - 1),
                        dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)
                ))
                .toList();
    }

    private static List<BookDto> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }
}
