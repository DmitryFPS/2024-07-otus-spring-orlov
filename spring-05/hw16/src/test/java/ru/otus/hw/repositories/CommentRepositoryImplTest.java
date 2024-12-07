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
import ru.otus.hw.entity.Comment;
import ru.otus.hw.entity.Genre;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для комментарий")
@DataJpaTest
class CommentRepositoryImplTest {
    @Autowired
    private CommentRepository commentRepository;

    private List<Comment> dbComment;

    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        final List<Author> dbAuthors = getDbAuthors();
        final List<Genre> dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
        dbComment = getDbComments(dbBooks);
    }

    @DisplayName("Загрузить комментарий по id")
    @ParameterizedTest
    @MethodSource("getDbComments")
    void testGetCommentById(final Comment expectedComment) {
        final Optional<Comment> actualComment = commentRepository.findById(expectedComment.getId());
        assertThat(actualComment)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedComment);
    }

    @DisplayName("Загрузить комментарии по книге")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void testGetCommentsByBook(final Book book) {
        final List<Comment> expectedComments = dbComment.stream()
                .filter(comment -> Objects.equals(comment.getBook().getId(), book.getId()))
                .toList();
        final List<Comment> actualComments = commentRepository.findAllByBookId(book.getId());

        assertThat(actualComments).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedComments);
    }

    @DisplayName("Сохранить новый комментарий")
    @Test
    void testUpdatedBookComment() {
        final Comment expectedComment = new Comment(null, "CommentNew", dbBooks.get(0));
        final Comment returnedComment = commentRepository.save(expectedComment);
        assertThat(returnedComment).isNotNull()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedComment);
    }

    @DisplayName("Сохранить измененный комментарий")
    @Test
    void testSaveNewComment() {
        final Comment expectedComment = new Comment(1L, "CommentNew", dbBooks.get(1));
        assertThat(commentRepository.findById(expectedComment.getId()))
                .isPresent();
        final Comment returnedComment = commentRepository.save(expectedComment);
        assertThat(returnedComment.getId())
                .isEqualTo(expectedComment.getId());
    }

    @DisplayName("Удалить комментарий")
    @Test
    void deleteComment() {
        final Optional<Comment> comment = commentRepository.findById(1L);
        assertThat(comment).isPresent();
        commentRepository.deleteById(1L);
        assertThat(commentRepository.findById(1L)).isEmpty();
    }

    private static List<Comment> getDbComments() {
        var books = getDbBooks();
        return getDbComments(books);
    }

    private static List<Comment> getDbComments(List<Book> books) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Comment(Long.valueOf(id),
                        "Comment_" + id,
                        books.get(id - 1)
                ))
                .toList();
    }

    private static List<Book> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }


    private static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Book(Long.valueOf(id),
                        "BookTitle_" + id,
                        dbAuthors.get(id - 1),
                        dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)
                ))
                .toList();
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
}
