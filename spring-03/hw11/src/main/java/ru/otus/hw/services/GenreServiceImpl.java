package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mapper.GenreMapper;
import ru.otus.hw.repositories.GenreRepository;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository repository;

    private final GenreMapper genreMapper;


    @Override
    public Flux<GenreDto> findAll() {
        return repository.findAll().map(genreMapper::genreToGenreDto);
    }

    @Override
    public Mono<GenreDto> findById(final String id) {
        return repository.findById(id)
                .map(genreMapper::genreToGenreDto)
                .switchIfEmpty(Mono.error(new NotFoundException(
                        String.format("Не удалось получить жанр по Id: %s", id))));
    }
}
