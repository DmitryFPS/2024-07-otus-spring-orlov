package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;

public interface CommentService {
    Mono<CommentDto> findById(final String id);

    Flux<CommentDto> findByBookId(final String bookId);

    Mono<CommentDto> create(final CommentCreateDto commentCreateDto);

    Mono<Void> deleteById(final String id);
}
