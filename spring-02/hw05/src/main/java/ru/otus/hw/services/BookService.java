package ru.otus.hw.services;

import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {
    Optional<Book> findById(final long id);

    List<Book> findAll();

    Book insert(final String title, final long authorId, final Set<Long> genresIds);

    Book update(final long id, final String title, final long authorId, final Set<Long> genresIds);

    void deleteById(final long id);
}
