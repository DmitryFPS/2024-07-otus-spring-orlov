package ru.otus.hw.repositories;

import ru.otus.hw.entity.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreRepository {
    List<Genre> findAll();

    Optional<Genre> findById(final Long id);

    List<Genre> findAllByIds(final Set<Long> ids);
}
