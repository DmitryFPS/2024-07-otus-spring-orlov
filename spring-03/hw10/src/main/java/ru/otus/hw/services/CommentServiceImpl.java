package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.entity.Book;
import ru.otus.hw.entity.Comment;
import ru.otus.hw.exceptions.NotFoundException;
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
        return commentRepository.findById(id)
                .map(commentMapper::commentToCommentDto)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Не удалось получить комментарий по Id: %d", id)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> findByBookId(final Long bookId) {
        final List<Comment> comments = commentRepository.findAllByBookId(bookId);
        if (comments.isEmpty()) {
            throw new NotFoundException(String.format("Не удалось получить комментарий по Id книги: %d", bookId));
        }
        return commentMapper.commentsToCommentsDto(comments);
    }

    @Transactional
    @Override
    public CommentDto create(final CommentCreateDto commentCreateDto) {
        final Comment comment = new Comment(
                null, commentCreateDto.getContent(), getBookById(commentCreateDto.getBookId()));
        return commentMapper.commentToCommentDto(commentRepository.save(comment));
    }

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
