package ru.otus.hw.mongock.test.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import org.springframework.stereotype.Component;
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

@Component
@ChangeLog(order = "001")
public class DatabaseChangelog {

    public final List<Genre> genres = Arrays.asList(
            new Genre("6727f790b378982e33453344", "Genre_1"),
            new Genre("5427f790b378982e3e743344", "Genre_2"),
            new Genre("6727f790b371082e4e743344", "Genre_3"),
            new Genre("6727f890b378982e3e747744", "Genre_4"),
            new Genre("8737f790b478982e3e743344", "Genre_5"));

    public final List<Author> authors = Arrays.asList(
            new Author("1747f790b378982e1e783344", "Author_1"),
            new Author("7747f791b371977e3e743344", "Author_2"),
            new Author("4727f791b378127e3e743344", "Author_3"));

    public final List<Book> books = Arrays.asList(
            new Book("g527f740b378982e3e241144", "BookTitle_1", authors.get(0),
                    Arrays.asList(genres.get(0), genres.get(1))),
            new Book("d727ff91bb78982e3e743311", "BookTitle_2", authors.get(1),
                    Arrays.asList(genres.get(2), genres.get(3))),
            new Book("sd27f790b378982e3e74gg44", "BookTitle_3", authors.get(2),
                    Collections.singletonList(genres.get(4))));

    public final List<Comment> comments = Arrays.asList(
            new Comment("n727v790c378982e3e74ee44", "Comment_1", books.get(0)),
            new Comment("hb27fg90b37h982e3e7433hh", "Comment_2", books.get(0)),
            new Comment("bv27fd90b3789f2e3e7433b4", "Comment_3", books.get(0)),
            new Comment("o727j790d37n982g3e7433jj", "Comment_4", books.get(1)),
            new Comment("7b27f7c0b3f8982e3e74ff44", "Comment_5", books.get(1)),
            new Comment("j7h7f790bgh7898sf3743fd4", "Comment_6", books.get(2)));


    @ChangeSet(order = "001", id = "dropDb", author = "orlov", runAlways = true)
    public void dropDb(final MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "init", author = "orlov")
    public void initialize(final AuthorRepository authorRepository, final BookRepository bookRepository,
                           final CommentRepository commentRepository, final GenreRepository genreRepository) {
        genreRepository.saveAll(genres).blockLast();
        authorRepository.saveAll(authors).blockLast();
        bookRepository.saveAll(books).blockLast();
        commentRepository.saveAll(comments).blockLast();
    }
}
