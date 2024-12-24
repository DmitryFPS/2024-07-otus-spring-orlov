package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.entity.Book;
import ru.otus.hw.entity.Comment;
import ru.otus.hw.exceptions.EntityNotFoundException;
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

    @Transactional(readOnly = true)
    @Override
    public CommentDto findById(final Long id) {
        return commentRepository.findById(id).map(commentMapper::commentToCommentDto).orElseThrow(
                () -> new EntityNotFoundException(String.format("Не удалось получить комментарий по Id: %d", id)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> findByBookId(final Long bookId) {
        return commentMapper.commentsToCommentsDto(commentRepository.findAllByBookId(bookId));
    }

    @Transactional
    @Override
    public CommentDto create(final String content, final Long bookId) {
        final Comment comment = new Comment(null, content, getBookById(bookId));
        return commentMapper.commentToCommentDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public CommentDto update(final Long id, final String content) {
        final Comment comment = getCommentById(id);
        comment.setCommentText(content);
        return commentMapper.commentToCommentDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public void deleteById(final Long id) {
        commentRepository.deleteById(id);
    }

    private Comment getCommentById(final Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий с id %d не найден".formatted(id)));
    }

    private Book getBookById(final Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Книга с id %d не найден".formatted(bookId)));
    }
}
