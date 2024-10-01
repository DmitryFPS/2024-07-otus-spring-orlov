package ru.otus.hw.services;

import ru.otus.hw.dto.BookDto;

import java.util.List;
import java.util.Set;

public interface BookService {
    BookDto findById(final Long id);

    List<BookDto> findAll();

    BookDto create(final String title, final Long authorId, final Set<Long> genresIds);

    BookDto update(final Long id, final String title, final Long authorId, final Set<Long> genresIds);

    void deleteById(final Long id);
}
