package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto findById(final String id);

    List<CommentDto> findByBookId(final String bookId);

    CommentDto create(final String content, final String bookId);

    CommentDto update(final String id, final String content);

    void deleteById(final String id);
}
