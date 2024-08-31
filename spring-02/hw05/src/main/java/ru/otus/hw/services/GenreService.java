package ru.otus.hw.services;

import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

public interface GenreService {
    List<Genre> findAll();

    Genre findById(final long id);

    List<Genre> findAllByIds(final Set<Long> ids);
}
