package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {
    private final GenreRepository genreRepository;

    private final NamedParameterJdbcOperations jdbc;


    @Override
    public Optional<Book> findById(final long id) {
        final String sql = "select " +
                "book.id as book_id, " +
                "book.title as book_title, " +
                "author.id as author_id," +
                "author.full_name as author_full_name," +
                "genres.id as genre_id, " +
                "genres.name as genre_name " +
                "from books book " +
                "join authors author on book.author_id = author.id " +
                "join books_genres bg on book.id = bg.book_id " +
                "join genres genres on bg.genre_id = genres.id " +
                "where book.id = :id";
        final Book query = jdbc.query(sql, Map.of("id", id), new BookResultSetExtractor());
        return Optional.ofNullable(query);
    }

    @Override
    public List<Book> findAll() {
        final List<Genre> genres = genreRepository.findAll();
        final List<BookGenreRelation> relations = getAllGenreRelations();
        final List<Book> books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(final Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(final long id) {
        jdbc.update("delete from books where id = :id", Map.of("id", id));
    }

    private List<Book> getAllBooksWithoutGenres() {
        final String sql = "select " +
                "book.id as book_id, " +
                "book.title as book_title, " +
                "author.id as author_id," +
                "author.full_name as author_full_name," +
                "from books book " +
                "join authors author on book.author_id = author.id ";
        return jdbc.query(sql, new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return jdbc.query("select bg.book_id, bg.genre_id from books_genres as bg", new BooksToAuthorsRowMapper());
    }

    private void mergeBooksInfo(final List<Book> booksWithoutGenres,
                                final List<Genre> genres,
                                final List<BookGenreRelation> relations) {
        // Добавить книгам (booksWithoutGenres) жанры (genres) в соответствии со связями (relations)
        final Map<Long, Book> booksMap = booksWithoutGenres.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Book::getId, book -> book));
        final Map<Long, Genre> genresMap = genres.stream()
                .collect(Collectors.toMap(Genre::getId, genre -> genre));

        for (final BookGenreRelation relation : relations) {
            if (relation != null) {
                final Book book = booksMap.get(relation.bookId());
                book.getGenres().add(genresMap.get(relation.genreId()));
            }
        }
    }

    private Book insert(final Book book) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final SqlParameterSource params = new MapSqlParameterSource(
                Map.of("bookTitle", book.getTitle(), "authorId", book.getAuthor().getId()));
        jdbc.update("insert into books (title, author_id) values (:bookTitle, :authorId)",
                params, keyHolder, new String[]{"id"});

        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(final Book book) {
        final int updatedRowsCount = jdbc.update(
                "update books set title = :title, author_id = :author_id where id = :id",
                Map.of("title", book.getTitle(), "author_id", book.getAuthor().getId(), "id", book.getId()));

        // Выбросить EntityNotFoundException если не обновлено ни одной записи в БД
        if (updatedRowsCount == 0) {
            throw new EntityNotFoundException(String.format("Книга с id: %s не была обновлена", book.getId()));
        }

        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private void batchInsertGenresRelationsFor(final Book book) {
        // Использовать метод batchUpdate
        final String sql = "insert into books_genres (book_id, genre_id) values (:bookId, :genreId)";
        final MapSqlParameterSource[] sources = book.getGenres().stream()
                .map(genre -> new MapSqlParameterSource(Map.of("bookId", book.getId(), "genreId", genre.getId())))
                .toArray(MapSqlParameterSource[]::new);
        jdbc.batchUpdate(sql, sources);
    }

    private void removeGenresRelationsFor(final Book book) {
        final String sql = "delete from books_genres where book_id = :book_id";
        jdbc.update(sql, Map.of("book_id", book.getId()));
    }

    private static class BooksToAuthorsRowMapper implements RowMapper<BookGenreRelation> {
        @Override
        public BookGenreRelation mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            return new BookGenreRelation(rs.getLong("book_id"), rs.getLong("genre_id"));
        }
    }

    private static class BookRowMapper implements RowMapper<Book> {
        @Override
        public Book mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final Author author = new Author(rs.getLong("author_id"), rs.getString("author_full_name"));
            return new Book(rs.getLong("book_id"), rs.getString("book_title"), author, new ArrayList<>());
        }
    }

    // Использовать для findById
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {
        @Override
        public Book extractData(final ResultSet rs) throws SQLException, DataAccessException {
            Book book = null;
            while (rs.next()) {
                if (book == null) {
                    book = new BookRowMapper().mapRow(rs, rs.getRow());
                }
                final Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("genre_name"));
                book.getGenres().add(genre);
            }
            return book;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
