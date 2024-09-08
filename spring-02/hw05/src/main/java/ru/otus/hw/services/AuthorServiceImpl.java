package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    @Override
    public List<Author> findAll() {
        final List<Author> authors = authorRepository.findAll();
        if (isEmpty(authors)) {
            throw new EntityNotFoundException("Не удалось получить всех авторов");
        }
        return authors;
    }

    @Override
    public Author findById(final long id) {
        return authorRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Не удалось получить автора по Id: %d", id)));
    }
}
