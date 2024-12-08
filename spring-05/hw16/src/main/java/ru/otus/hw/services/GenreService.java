package ru.otus.hw.services;

import ru.otus.hw.dto.GenreDto;

import java.util.List;
import java.util.Set;

public interface GenreService {
    List<GenreDto> findAll();

    GenreDto findById(final Long id);

    List<GenreDto> findAllByIds(final Set<Long> ids);
}
