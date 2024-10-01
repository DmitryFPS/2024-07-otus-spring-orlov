package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.GenreMapper;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository repository;

    private final GenreMapper genreMapper;

    @Transactional(readOnly = true)
    @Override
    public List<GenreDto> findAll() {
        return genreMapper.genresToGenresDto(repository.findAll());
    }

    @Transactional(readOnly = true)
    @Override
    public GenreDto findById(final Long id) {
        return repository.findById(id).map(genreMapper::genreToGenreDto).orElseThrow(
                () -> new EntityNotFoundException(String.format("Не удалось получить жанр по Id: %d", id)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<GenreDto> findAllByIds(final Set<Long> ids) {
        return genreMapper.genresToGenresDto(repository.findAllByIdIn(ids));
    }
}
