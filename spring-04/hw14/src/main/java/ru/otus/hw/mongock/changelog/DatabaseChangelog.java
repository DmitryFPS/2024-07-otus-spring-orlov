package ru.otus.hw.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import ru.otus.hw.model.Author;
import ru.otus.hw.model.Book;
import ru.otus.hw.model.Comment;
import ru.otus.hw.model.Genre;
import ru.otus.hw.repositories.mongo.MongoAuthorRepository;
import ru.otus.hw.repositories.mongo.MongoBookRepository;
import ru.otus.hw.repositories.mongo.MongoCommentRepository;
import ru.otus.hw.repositories.mongo.MongoGenreRepository;

import java.util.Arrays;
import java.util.List;

@ChangeLog(order = "001")
public class DatabaseChangelog {

    private List<Genre> getGenres() {
        return Arrays.asList(
                new Genre("28665959-1622-4fb7-8a5a-a0ee41d63086", "Genre_1"),
                new Genre("2396c697-4766-4058-aaf5-e331398b9449", "Genre_2"),
                new Genre("aea2a3bb-91e8-49d8-aa3b-bd8b5bca417b", "Genre_3"));
    }

    private List<Author> getAuthors() {
        return Arrays.asList(
                new Author("8cab7218-6c9c-4c7d-96c1-ea6c1169786b", "Author_1"),
                new Author("c2a77aea-8803-4dda-a7f0-57335d185935", "Author_2"),
                new Author("4bf377b5-2b6d-47b2-8d25-3cd77fa84a11", "Author_3"));
    }

    private List<Book> getBooks() {
        return Arrays.asList(
                new Book("08338ac5-c454-403e-a006-171d1f790f75", "BookTitle_1",
                        getAuthors().get(2), getGenres().get(1)),
                new Book("826d400a-3c4b-4225-9b9d-5981a9531649", "BookTitle_2",
                        getAuthors().get(0), getGenres().get(2)),
                new Book("7b4a8ffd-9925-46e6-9a11-a9b5cc816d7f", "BookTitle_3",
                        getAuthors().get(1), getGenres().get(0)));
    }

    private List<Comment> getComments() {
        return Arrays.asList(
                new Comment("4715e2d5-367c-4159-8b67-8546407beb64", "Comment_1", getBooks().get(2)),
                new Comment("b36a2f01-c52e-4e09-8ecf-151ba2ed9900", "Comment_2", getBooks().get(1)),
                new Comment("1f76d89d-3e4a-47c2-b3c1-0c87a55d0676", "Comment_3", getBooks().get(0)),
                new Comment("0950b359-c7ce-4e7d-96f8-682b13443ea7", "Comment_4", getBooks().get(0)),
                new Comment("2cbd02e3-7807-4669-b100-f6712d21df4f", "Comment_5", getBooks().get(1)),
                new Comment("f0474e0e-1ea2-4bf3-a4f1-8fc6dfcbeb97", "Comment_6", getBooks().get(0)));
    }

    @ChangeSet(order = "001", id = "init", author = "orlov")
    public void initialize(final MongoAuthorRepository mongoAuthorRepository,
                           final MongoBookRepository mongoBookRepository,
                           final MongoCommentRepository mongoCommentRepository,
                           final MongoGenreRepository mongoGenreRepository) {
        mongoGenreRepository.saveAll(getGenres());
        mongoAuthorRepository.saveAll(getAuthors());
        mongoBookRepository.saveAll(getBooks());
        mongoCommentRepository.saveAll(getComments());
    }
}
