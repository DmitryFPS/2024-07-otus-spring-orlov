package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.entity.Author;
import ru.otus.hw.entity.Book;
import ru.otus.hw.entity.Genre;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;


    @Transactional(readOnly = true)
    @Override
    public BookDto findById(final Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::bookToBookDto)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Не удалось получить книгу по Id: %d", id)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookDto> findAll() {
        return bookMapper.booksToBooksDto(bookRepository.findAll());
    }

    @Transactional
    @Override
    public BookDto create(final BookCreateDto book) {
        final Book bookToSave = new Book(null,
                book.getTitle(), getAuthorById(book.getAuthorId()),
                getGenresByIds(book.getGenreIds()));
        return bookMapper.bookToBookDto(bookRepository.save(bookToSave));
    }

    @Transactional
    @Override
    public BookDto update(final BookUpdateDto bookDto) {
        final Book book = getBookById(bookDto.getId());
        book.setTitle(bookDto.getTitle());
        book.setAuthor(getAuthorById(bookDto.getAuthorId()));
        book.setGenres(getGenresByIds(bookDto.getGenreIds()));
        return bookMapper.bookToBookDto(bookRepository.save(book));
    }

    @Transactional
    @Override
    public void deleteById(final Long id) {
        bookRepository.deleteById(id);
    }

    private Book getBookById(final Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Книга с id %d не найдена".formatted(bookId)));
    }

    private Author getAuthorById(final Long authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Автор с id %d не найден".formatted(authorId)));
    }

    private List<Genre> getGenresByIds(final Set<Long> genresIds) {
        final List<Genre> genres = genreRepository.findAllByIdIn(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new NotFoundException("Один или все жанры с ids %s не найдены".formatted(genresIds));
        }
        return genres;
    }
}
