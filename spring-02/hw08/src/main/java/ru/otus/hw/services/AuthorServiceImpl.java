package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.AuthorMapper;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    @Override
    public List<AuthorDto> findAll() {
        return authorMapper.authorsToAuthorsDto(authorRepository.findAll());
    }

    @Override
    public AuthorDto findById(final String id) {
        return authorRepository.findById(id)
                .map(authorMapper::authorToAuthorDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Не удалось получить автора по Id: %s", id)));
    }
}
