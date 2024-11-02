package ru.otus.hw.services;

import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;

import java.util.List;

public interface BookService {
    BookDto findById(final Long id);

    List<BookDto> findAll();

    BookDto create(final BookCreateDto book);

    BookDto update(final BookUpdateDto book);

    void deleteById(final Long id);
}
