package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mapper.AuthorMapper;
import ru.otus.hw.repositories.AuthorRepository;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;


    @Override
    public Flux<AuthorDto> findAll() {
        return authorRepository.findAll().map(authorMapper::authorToAuthorDto);
    }

    @Override
    public Mono<AuthorDto> findById(final String id) {
        return authorRepository.findById(id)
                .map(authorMapper::authorToAuthorDto)
                .switchIfEmpty(Mono.error(new NotFoundException(
                        String.format("Не удалось получить автора по Id: %s", id))));
    }
}
