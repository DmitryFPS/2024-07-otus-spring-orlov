package ru.otus.hw.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import ru.otus.hw.model.Author;
import ru.otus.hw.model.Book;
import ru.otus.hw.model.Comment;
import ru.otus.hw.model.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ChangeLog(order = "001")
public class DatabaseChangelog {

    @ChangeSet(order = "001", id = "init", author = "orlov")
    public void initialize(final AuthorRepository authorRepository, final BookRepository bookRepository,
                           final CommentRepository commentRepository, final GenreRepository genreRepository) {
        final List<Genre> genres = Arrays.asList(new Genre("1", "Genre_1"), new Genre("2", "Genre_2"),
                new Genre("3", "Genre_3"), new Genre("4", "Genre_4"), new Genre("5", "Genre_5"));
        genreRepository.saveAll(genres);

        final List<Author> authors = Arrays.asList(new Author("1", "Author_1"), new Author("2", "Author_2"),
                new Author("3", "Author_3"));
        authorRepository.saveAll(authors);

        final List<Book> books = Arrays.asList(
                new Book("1", "BookTitle_1", authors.get(0), Arrays.asList(genres.get(0), genres.get(1))),
                new Book("2", "BookTitle_2", authors.get(1), Arrays.asList(genres.get(2), genres.get(3))),
                new Book("3", "BookTitle_3", authors.get(2), Collections.singletonList(genres.get(4))));
        bookRepository.saveAll(books);

        final List<Comment> comments = Arrays.asList(
                new Comment("1", "Comment_1", books.get(0)), new Comment("2", "Comment_2", books.get(0)),
                new Comment("3", "Comment_3", books.get(0)), new Comment("4", "Comment_4", books.get(1)),
                new Comment("5", "Comment_5", books.get(1)), new Comment("6", "Comment_6", books.get(2)));
        commentRepository.saveAll(comments);
    }
}
