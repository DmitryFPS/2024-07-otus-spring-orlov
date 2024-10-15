package ru.otus.hw.services;

import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookEditDto;

import java.util.List;
import java.util.Set;

public interface BookService {
    BookDto findById(final Long id);

    List<BookDto> findAll();

    BookDto create(final BookEditDto book);

    BookDto update(final BookEditDto book);

    void deleteById(final Long id);

    List<BookDto> findAllByAuthorId(final long authorId);
}
