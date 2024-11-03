package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.model.Book;
import ru.otus.hw.model.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;


    @Override
    public Mono<CommentDto> findById(final String id) {
        return commentRepository.findById(id)
                .map(commentMapper::commentToCommentDto)
                .switchIfEmpty(Mono.error(new NotFoundException(
                        String.format("Не удалось получить комментарий по Id: %s", id))));
    }

    @Override
    public Flux<CommentDto> findByBookId(final String bookId) {
        return commentRepository.findByBookId(bookId).map(commentMapper::commentToCommentDto);
    }

    @Transactional
    @Override
    public Mono<CommentDto> create(final CommentCreateDto commentCreateDto) {
        return getBookById(commentCreateDto.getBookId())
                .flatMap(book -> {
                    final Comment comment = new Comment(null, commentCreateDto.getContent(), book);
                    return commentRepository.save(comment);
                })
                .map(commentMapper::commentToCommentDto);
    }

    @Transactional
    @Override
    public Mono<Void> deleteById(final String id) {
        return commentRepository.deleteById(id);
    }

    private Mono<Book> getBookById(final String bookId) {
        return bookRepository.findById(bookId)
                .switchIfEmpty(Mono.error(new NotFoundException("Книга с id %s не найден".formatted(bookId))));
    }
}
