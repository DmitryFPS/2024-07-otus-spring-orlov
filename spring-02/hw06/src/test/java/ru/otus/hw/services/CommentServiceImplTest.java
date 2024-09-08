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
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.CommentMapperImpl;
import ru.otus.hw.repositories.BookRepositoryImpl;
import ru.otus.hw.repositories.CommentRepositoryImpl;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Service для работы с Comments")
@DataJpaTest
@Import({CommentServiceImpl.class,
        CommentMapperImpl.class,
        BookRepositoryImpl.class,
        CommentRepositoryImpl.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class CommentServiceImplTest {
    @Autowired
    private CommentServiceImpl serviceTest;

    private List<CommentDto> dbComment;

    @BeforeEach
    void setUp() {
        dbComment = getDbComments();
    }

    @DisplayName("Проверяем получение комментария по id")
    @ParameterizedTest
    @MethodSource("getDbComments")
    void testGetCommentById(final CommentDto expectedComment) {
        final CommentDto actualComment = serviceTest.findById(expectedComment.getId());
        assertThat(actualComment).isEqualTo(expectedComment);
    }

    @DisplayName("Проверяем получение комментарий по Book")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void testGetCommentsByBook(final BookDto expectedBook) {
        final List<CommentDto> actualComments = serviceTest.findByBookId(expectedBook.getId());
        final int commentIndex = (int) (expectedBook.getId() - 1);
        assertThat(actualComments).containsOnly(dbComment.get(commentIndex));
    }

    @DisplayName("Проверяем сохранение нового комментария")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testSaveBookComment() {
        final CommentDto expectedComment = new CommentDto(null, "CommentNew");
        final CommentDto returnedComment = serviceTest.create(expectedComment.getCommentText(), 1L);
        assertThat(returnedComment)
                .isNotNull()
                .matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedComment);
    }

    @DisplayName("Проверяем обновление изменения комментария")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testUpdatedBookComment() {
        final CommentDto expectedComment = new CommentDto(1L, "CommentUpdate");
        final CommentDto returnedComment =
                serviceTest.update(expectedComment.getId(), expectedComment.getCommentText());

        assertThat(returnedComment)
                .isNotNull()
                .isEqualTo(expectedComment);
    }

    @DisplayName("Проверка удаления комментария")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testDelete() {
        final CommentDto comment = serviceTest.findById(1L);
        serviceTest.deleteById(comment.getId());

        assertThatThrownBy(() -> serviceTest.findById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Не удалось получить комментарий по Id: 1");
    }

    private static List<CommentDto> getDbComments() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new CommentDto(Long.valueOf(id), "Comment_" + id))
                .toList();
    }

    private static List<BookDto> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
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
}
