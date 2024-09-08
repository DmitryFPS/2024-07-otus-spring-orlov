package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository repository;

    @Override
    public List<Genre> findAll() {
        final List<Genre> genres = repository.findAll();
        if (genres.isEmpty()) {
            throw new EntityNotFoundException("Ошибка получения всех жанров");
        }
        return genres;
    }

    @Override
    public Genre findById(final long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Не удалось получить жанр по Id: %d", id)));
    }

    @Override
    public List<Genre> findAllByIds(final Set<Long> ids) {
        return repository.findAllByIds(ids);
    }
}
