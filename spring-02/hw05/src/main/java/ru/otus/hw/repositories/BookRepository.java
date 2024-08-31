package ru.otus.hw.repositories;

import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Optional<Book> findById(final long id);

    List<Book> findAll();

    Book save(final Book book);

    void deleteById(final long id);
}
