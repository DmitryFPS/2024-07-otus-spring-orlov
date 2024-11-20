package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exception.NotFoundException;
import ru.otus.hw.mapper.BookMapperImpl;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DataJpaTest
@Import({BookMapperImpl.class, BookServiceImpl.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class BookServiceImplTest {
    @Autowired
    private BookServiceImpl serviceTest;

    @MockBean
    private AclServiceWrapperServiceImpl aclService;


    @Test
    void testGetBookById() {
        final BookDto expectedBook = getBookDtos().get(0);
        final BookDto actualBook = serviceTest.findById(expectedBook.getId());
        assertThat(actualBook).isEqualTo(expectedBook);
    }

    @Test
    void testGetBooksList() {
        final List<BookDto> actualBooks = serviceTest.findAll();
        final List<BookDto> expectedBooks = getBookDtos();
        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testSaveNewBook() {
        final BookDto expectedBook = getBookDtos().get(0);

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

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testUpdatedBook() {
        final BookDto expectedBook = getBookDtos().get(0);

        final BookUpdateDto bookUpdateDto = new BookUpdateDto(expectedBook.getId(),
                expectedBook.getTitle(), expectedBook.getAuthor().getId(),
                expectedBook.getGenres().stream().map(GenreDto::getId).collect(Collectors.toSet()));
        final BookDto returnedBook = serviceTest.update(bookUpdateDto);

        assertThat(returnedBook)
                .isNotNull()
                .isEqualTo(expectedBook);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testDeleteBook() {
        final BookDto book = serviceTest.findById(1L);
        serviceTest.deleteById(book.getId());
        assertThatThrownBy(() -> serviceTest.findById(1L))
                .isInstanceOf(NotFoundException.class);
    }


    private List<BookDto> getBookDtos() {
        final List<GenreDto> genres_1_2 = List.of(
                new GenreDto(1L, "Роман"), new GenreDto(2L, "Эпопея"));
        final AuthorDto authorDto_1 = new AuthorDto(1L, "Лев Николаевич Толстой");
        final BookDto book_1 = new BookDto(1L, "Война и мир", authorDto_1, genres_1_2);

        final List<GenreDto> genres_3_4 = List.of(
                new GenreDto(3L, "Рассказ"), new GenreDto(4L, "Повесть"));
        final AuthorDto authorDto_2 = new AuthorDto(2L, "Антон Павлович Чехов");
        final BookDto book_2 = new BookDto(2L, "Хамелеон", authorDto_2, genres_3_4);

        final List<GenreDto> genres_5_6 = List.of(
                new GenreDto(5L, "Пьеса"), new GenreDto(6L, "Исповедь"));
        final AuthorDto authorDto_3 = new AuthorDto(3L, "Иван Алексеевич Бунин");
        final BookDto book_3 = new BookDto(3L, "Деревня", authorDto_3, genres_5_6);

        return List.of(book_1, book_2, book_3);
    }
}
