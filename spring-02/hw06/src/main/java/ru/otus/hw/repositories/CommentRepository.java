package ru.otus.hw.repositories;

import ru.otus.hw.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Optional<Comment> findById(final Long id);

    List<Comment> findByBookId(final Long bookId);

    Comment save(final Comment comment);

    void deleteById(final Long id);
}
