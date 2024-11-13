package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;

public interface BookService {
    Mono<BookDto> findById(final String id);

    Flux<BookDto> findAll();

    Mono<BookDto> create(final BookCreateDto book);

    Mono<BookDto> update(final BookUpdateDto book);

    Mono<Void> deleteById(final String id);
}
