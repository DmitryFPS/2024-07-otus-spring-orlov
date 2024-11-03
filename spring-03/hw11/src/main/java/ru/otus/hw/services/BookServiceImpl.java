package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.model.Author;
import ru.otus.hw.model.Book;
import ru.otus.hw.model.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;


    @Override
    public Mono<BookDto> findById(final String id) {
        return bookRepository.findById(id)
                .map(bookMapper::bookToBookDto)
                .switchIfEmpty(Mono.error(new NotFoundException(
                        String.format("Не удалось получить книгу по Id: %s", id))));
    }

    @Override
    public Flux<BookDto> findAll() {
        return bookRepository.findAll().map(bookMapper::bookToBookDto);
    }

    @Transactional
    @Override
    public Mono<BookDto> create(final BookCreateDto bookDto) {
        return Mono.zip(
                        getAuthorById(bookDto.getAuthorId()),
                        getGenresByIds(bookDto.getGenreIds())
                                .collectList()
                )
                .flatMap(tuple -> {
                    final Author author = tuple.getT1();
                    final List<Genre> genres = tuple.getT2();
                    final Book bookToSave = new Book(null, bookDto.getTitle(), author, genres);
                    return bookRepository.save(bookToSave);
                })
                .map(bookMapper::bookToBookDto);
    }

    @Transactional
    @Override
    public Mono<BookDto> update(final BookUpdateDto bookDto) {
        return getBookById(bookDto.getId())
                .flatMap(book -> {
                    book.setTitle(bookDto.getTitle());
                    return getAuthorById(bookDto.getAuthorId())
                            .flatMap(author -> {
                                book.setAuthor(author);
                                return getGenresByIds(bookDto.getGenreIds())
                                        .collectList()
                                        .map(genres -> {
                                            book.setGenres(genres);
                                            return book;
                                        });
                            });
                })
                .flatMap(bookRepository::save)
                .map(bookMapper::bookToBookDto);
    }

    @Transactional
    @Override
    public Mono<Void> deleteById(final String id) {
        return bookRepository.deleteById(id);
    }

    private Mono<Book> getBookById(final String bookId) {
        return bookRepository.findById(bookId)
                .switchIfEmpty(Mono.error(new NotFoundException("Книга с id %s не найдена".formatted(bookId))));
    }

    private Mono<Author> getAuthorById(final String authorId) {
        return authorRepository.findById(authorId)
                .switchIfEmpty(Mono.error(new NotFoundException("Автор с id %s не найден".formatted(authorId))));
    }

    private Flux<Genre> getGenresByIds(final Set<String> genresIds) {
        return genreRepository.findAllByIdIn(genresIds)
                .switchIfEmpty(Flux.error(new NotFoundException("Один или все жанры с ids %s не найдены"
                        .formatted(genresIds))));
    }
}
