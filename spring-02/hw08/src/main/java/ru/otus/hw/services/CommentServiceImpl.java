package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.model.Book;
import ru.otus.hw.model.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;


    @Override
    public CommentDto findById(final String id) {
        return commentRepository.findById(id).map(commentMapper::commentToCommentDto).orElseThrow(
                () -> new EntityNotFoundException(String.format("Не удалось получить комментарий по Id: %s", id)));
    }

    @Override
    public List<CommentDto> findByBookId(final String bookId) {
        return commentMapper.commentsToCommentsDto(commentRepository.findAllByBookId(bookId));
    }

    @Transactional
    @Override
    public CommentDto create(final String content, final String bookId) {
        final Comment comment = new Comment(null, content, getBookById(bookId));
        return commentMapper.commentToCommentDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public CommentDto update(final String id, final String content) {
        final Comment comment = getCommentById(id);
        comment.setCommentText(content);
        return commentMapper.commentToCommentDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public void deleteById(final String id) {
        commentRepository.deleteById(id);
    }

    private Comment getCommentById(final String id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий с id %s не найден".formatted(id)));
    }

    private Book getBookById(final String bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Книга с id %s не найден".formatted(bookId)));
    }
}
