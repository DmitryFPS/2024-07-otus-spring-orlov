package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.entity.Author;
import ru.otus.hw.entity.Book;
import ru.otus.hw.entity.Genre;
import ru.otus.hw.exceptions.EntityNotFoundException;
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
        return bookRepository.findById(id).map(bookMapper::bookToBookDto).orElseThrow(
                () -> new EntityNotFoundException(String.format("Не удалось получить книгу по Id: %d", id)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookDto> findAll() {
        return bookMapper.booksToBooksDto(bookRepository.findAll());
    }

    @Transactional
    @Override
    public BookDto create(final String title, final Long authorId, final Set<Long> genresIds) {
        final Book book = new Book(null, title, getAuthorById(authorId), getGenresByIds(genresIds));
        return bookMapper.bookToBookDto(bookRepository.save(book));
    }

    @Transactional
    @Override
    public BookDto update(final Long id, final String title, final Long authorId, final Set<Long> genresIds) {
        final Book book = getBookById(id);
        book.setTitle(title);
        book.setAuthor(getAuthorById(authorId));
        book.setGenres(getGenresByIds(genresIds));
        return bookMapper.bookToBookDto(bookRepository.save(book));
    }

    @Transactional
    @Override
    public void deleteById(final Long id) {
        bookRepository.deleteById(id);
    }

    private Book getBookById(final Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Книга с id %d не найдена".formatted(bookId)));
    }

    private Author getAuthorById(final Long authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Автор с id %d не найден".formatted(authorId)));
    }

    private List<Genre> getGenresByIds(final Set<Long> genresIds) {
        final List<Genre> genres = genreRepository.findAllByIdIn(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("Один или все жанры с ids %s не найдены".formatted(genresIds));
        }
        return genres;
    }
}
