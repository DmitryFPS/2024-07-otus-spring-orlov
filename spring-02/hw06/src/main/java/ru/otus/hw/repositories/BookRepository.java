package ru.otus.hw.repositories;

import ru.otus.hw.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Optional<Book> findById(final Long id);

    List<Book> findAll();

    Book save(final Book book);

    void deleteById(final Long id);
}
