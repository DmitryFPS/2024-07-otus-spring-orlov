package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto findById(final Long id);

    List<CommentDto> findByBookId(final Long bookId);

    CommentDto create(final String content, final Long bookId);

    void deleteById(final Long id);
}
