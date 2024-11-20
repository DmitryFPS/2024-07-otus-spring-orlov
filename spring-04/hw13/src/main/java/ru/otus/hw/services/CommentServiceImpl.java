package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.entity.Book;
import ru.otus.hw.entity.Comment;
import ru.otus.hw.exception.NotFoundException;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional(readOnly = true)
    @Override
    public CommentDto findById(final Long id) {
        return commentRepository.findById(id)
                .map(commentMapper::commentToCommentDto)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Не удалось получить комментарий по Id: %d", id)));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> findByBookId(final Long bookId) {
        return commentMapper.commentsToCommentsDto(commentRepository.findAllByBookId(bookId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional
    @Override
    public CommentDto create(final String content, final Long bookId) {
        final Comment comment = new Comment(null, content, getBookById(bookId));
        return commentMapper.commentToCommentDto(commentRepository.save(comment));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public void deleteById(final Long id) {
        commentRepository.deleteById(id);
    }

    private Book getBookById(final Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Книга с id %d не найден".formatted(bookId)));
    }
}
