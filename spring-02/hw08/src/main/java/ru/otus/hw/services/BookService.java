package ru.otus.hw.services;

import ru.otus.hw.dto.BookDto;

import java.util.List;
import java.util.Set;

public interface BookService {
    BookDto findById(final String id);

    List<BookDto> findAll();

    BookDto create(final String title, final String authorId, final Set<String> genresIds);

    BookDto update(final String id, final String title, final String authorId, final Set<String> genresIds);

    void deleteById(final String id);
}
