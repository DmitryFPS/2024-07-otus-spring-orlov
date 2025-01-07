package ru.otus.hw.services;

import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto findById(final Long id);

    List<CommentDto> findByBookId(final Long bookId);

    CommentDto create(final CommentCreateDto commentCreateDto);

    void deleteById(final Long id);
}
