package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.AuthorMapperImpl;
import ru.otus.hw.mapper.BookMapperImpl;
import ru.otus.hw.mapper.CommentMapperImpl;
import ru.otus.hw.mapper.GenreMapperImpl;
import ru.otus.hw.model.Author;
import ru.otus.hw.model.Book;
import ru.otus.hw.model.Genre;
import ru.otus.hw.mongock.events.MongoBookCascadeDeleteEventListener;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Service для работы с Books")
@DataMongoTest
@Import({CommentServiceImpl.class, BookServiceImpl.class,
        AuthorMapperImpl.class, GenreMapperImpl.class,
        BookMapperImpl.class, CommentMapperImpl.class,
        MongoBookCascadeDeleteEventListener.class})
@Transactional(propagation = Propagation.NEVER)
class BookServiceImplTest {

    @Autowired
    private BookServiceImpl serviceTest;

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private BookMapperImpl bookMapperImpl;

    @Autowired
    private AuthorMapperImpl authorMapperImpl;

    @Autowired
    private GenreMapperImpl genreMapperImpl;


    @ParameterizedTest
    @MethodSource("GetBookByIdTest")
    void testGetBookById(final String id) {
        final BookDto actualBook = serviceTest.findById(id);
        final BookDto expectedBook = bookMapperImpl.bookToBookDto(mongoOperations.findById(id, Book.class));
        assertThat(actualBook).usingRecursiveComparison().isEqualTo(expectedBook);
    }

    private static Object[] GetBookByIdTest() {
        return new Object[]{
                new Object[]{"1"},
                new Object[]{"2"},
                new Object[]{"3"}
        };
    }


    @Test
    void testGetBooksList() {
        final List<BookDto> actualBooks = serviceTest.findAll();
        final List<BookDto> expectedBooks = bookMapperImpl.booksToBooksDto(mongoOperations.findAll(Book.class));
        assertThat(actualBooks).usingRecursiveComparison().isEqualTo(expectedBooks);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testSaveNewBook() {
        final AuthorDto author = authorMapperImpl.authorToAuthorDto(mongoOperations.findById("1", Author.class));
        final GenreDto genre_1 = genreMapperImpl.genreToGenreDto(mongoOperations.findById("1", Genre.class));
        final GenreDto genre_2 = genreMapperImpl.genreToGenreDto(mongoOperations.findById("3", Genre.class));

        final BookDto expectedBook = new BookDto(null, "BookTitleNew", author,
                List.of(genre_1, genre_2));
        final BookDto returnedBook = serviceTest.create(expectedBook.getTitle(),
                expectedBook.getAuthor().getId(),
                expectedBook.getGenres().stream().map(GenreDto::getId).collect(Collectors.toSet()));
        assertThat(returnedBook)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedBook);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testUpdatedBook() {
        final AuthorDto author = authorMapperImpl.authorToAuthorDto(mongoOperations.findById("2", Author.class));
        final GenreDto genre_4 = genreMapperImpl.genreToGenreDto(mongoOperations.findById("4", Genre.class));
        final GenreDto genre_5 = genreMapperImpl.genreToGenreDto(mongoOperations.findById("5", Genre.class));

        final BookDto expectedBook = new BookDto("1", "BookTitleUpdate", author, List.of(genre_4, genre_5));
        final BookDto returnedBook = serviceTest.update(expectedBook.getId(),
                expectedBook.getTitle(),
                expectedBook.getAuthor().getId(),
                expectedBook.getGenres().stream().map(GenreDto::getId).collect(Collectors.toSet())
        );

        assertThat(returnedBook)
                .isNotNull()
                .isEqualTo(expectedBook);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testDeleteBook() {
        final BookDto book = serviceTest.findById("1");
        assertThat(book).isNotNull();

        final List<CommentDto> commentsBefore = commentService.findByBookId(book.getId());
        assertThat(commentsBefore).isNotNull();
        assertThat(commentsBefore).isNotEmpty();

        serviceTest.deleteById(book.getId());

        final List<CommentDto> commentsAfter = commentService.findByBookId(book.getId());
        assertThat(commentsAfter).isEmpty();

        assertThatThrownBy(() -> serviceTest.findById("1"))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
