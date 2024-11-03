package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mapper.BookMapperImpl;
import ru.otus.hw.model.Book;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({BookServiceImpl.class, CommentServiceImpl.class, BookMapperImpl.class})
class BookServiceImplTest {

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private BookServiceImpl bookService;

    @Autowired
    private BookMapperImpl bookMapper;

    private static final String BOOK_ID = "g527f740b378982e3e241144";

    private static final String GENRE_ID = "6727f790b378982e33453344";

    private static final String AUTHOR_ID = "7747f791b371977e3e743344";

    private static final Set<String> GENRES_ID = Set.of("5427f790b378982e3e743344", "6727f890b378982e3e747744");


    @Test
    void testFindById() {
        final Mono<BookDto> actualBook = bookService.findById(BOOK_ID);
        final BookDto expectedBook = bookMapper.bookToBookDto(mongoOperations.findById(BOOK_ID, Book.class));

        assertEquals(expectedBook, actualBook.block());
    }

    @Test
    void testFindAll() {
        final Flux<BookDto> actualBook = bookService.findAll();
        final List<BookDto> expectedBooks = bookMapper.booksToBooksDto(mongoOperations.findAll(Book.class));

        assertThat(actualBook.collectList().block()).usingRecursiveComparison().isEqualTo(expectedBooks);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testSaveNewBook() {
        final BookCreateDto bookCreateDto = new BookCreateDto("title", AUTHOR_ID, GENRES_ID);

        final Mono<BookDto> bookDtoMono = bookService.create(bookCreateDto);
        final Book book = mongoOperations
                .findById(Objects.requireNonNull(bookDtoMono.block()).getId(), Book.class);
        final BookDto expectedBook = bookMapper.bookToBookDto(book);

        assertThat(bookDtoMono.block())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedBook);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testUpdatedBook() {
        final BookDto book = bookMapper.bookToBookDto(
                mongoOperations.findById(BOOK_ID, Book.class));

        final BookUpdateDto updateDto = new BookUpdateDto(
                book.getId(), "BookTitle_update", book.getAuthor().getId(), Set.of(GENRE_ID));

        final Mono<BookDto> actualUpdate = bookService.update(updateDto);

        assertThat(actualUpdate.block())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("title")
                .ignoringFields("genres")
                .isEqualTo(book);

        assertThat(actualUpdate.block()).isNotNull()
                .extracting(BookDto::getTitle)
                .isEqualTo("BookTitle_update");

        assertThat(Objects.requireNonNull(actualUpdate.block()).getGenres())
                .isNotEmpty()
                .extracting(GenreDto::getId)
                .containsExactlyInAnyOrder(GENRE_ID);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testDeleteBook() {
        final Mono<BookDto> book = bookService.findById(BOOK_ID);
        assertThat(book.block()).isNotNull();
        final Set<String> genresIdToBook = Objects.requireNonNull(book.block()).getGenres().stream()
                .map(GenreDto::getId).collect(Collectors.toSet());
        assertThat(genresIdToBook).isNotNull();
        assertThat(genresIdToBook).isNotEmpty();

        final Mono<Void> delete = bookService.deleteById(BOOK_ID);
        StepVerifier.create(delete)
                .expectComplete()
                .verify();

        assertThatThrownBy(() -> bookService.findById(BOOK_ID).block())
                .isInstanceOf(RuntimeException.class);

        for (final String genreId : genresIdToBook) {
            assertThatThrownBy(() -> commentService.findById(genreId).block())
                    .isInstanceOf(RuntimeException.class);
        }
    }
}
